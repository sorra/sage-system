'use strict';

$(document).ready(function(){
  $(document).on('click focus', 'a', function(){this.blur()})

  if ($('#front-map').length > 0) {
    window.frontMap = $.parseJSON($('#front-map').text());
  } else {
    window.frontMap = {};
  }

  if ($('#user-self-json').length > 0) {
    window.userSelf = $.parseJSON($('#user-self-json').text());
    $.get("/notifications/unread-counts").done(function(data){
      var notifCountLines = []
      parseNotifCount('FORWARDED', data, notifCountLines)
      parseNotifCount('COMMENTED', data, notifCountLines)
      parseNotifCount('REPLIED', data, notifCountLines)
      parseNotifCount('MENTIONED_TWEET', data, notifCountLines)
      parseNotifCount('MENTIONED_COMMENT', data, notifCountLines)
      parseNotifCount('FOLLOWED', data, notifCountLines)
      var notifCountHtml = ''
      for (var i in notifCountLines) {
        notifCountHtml += '<a href="/notifications/unread" style="display: block">'+notifCountLines[i]+'</a>'
      }
      if (notifCountHtml.length > 0) {
        $('.navbar .search').popover({
          html: true,
          placement: 'bottom',
          trigger: 'manual',
          selector: '#notif-count-popover',
          content: notifCountHtml
        }).popover('show')
      }
    })
  }

  if ($('#tag-tree-json').length > 0) {
    window.tagTree = $.parseJSON($('#tag-tree-json').text());
    var $lnk = $('#nav-tags')
      .click(function(event){
        event.preventDefault();
        $(this).popover('toggle');
      });
    buildNavTagTree($lnk, window.tagTree);
  }
});

function buildNavTagTree($lnk, tagTree) {
  var $navTagTree = $('<div>')
  var $createTag = $('<button class="create-tag btn btn-warning">新建</button>').appendTo($navTagTree)

  var $dialog = $('#new-tag-dialog')
  $dialog.find('input[name=name]').val('')
  $dialog.find('input[name=parent-id]').val('')

  $dialog.find('.submit').click(function() {
    var name = $dialog.find('#name').val()
    var parentId = $dialog.find('#parent-id').val()
    if (name && name.length > 0 && parentId && parentId.length > 0) {
      $.post('/tags/new', {
        name: name,
        parentId: parentId
      })
    }
    $dialog.find('#name').val('')
    $dialog.find('#parent-id').val('')
    $dialog.modal('hide')
  })

  $(document).delegate('.create-tag', 'click', function(){
    $dialog.modal('show')
  })

  tag_tree(tagTree).appendTo($navTagTree)

  $(document).delegate('.tag-tree .tag-label', 'click', function(){
    $lnk.popover('hide')
  })
  $lnk.popover({
    html: true,
    placement: 'bottom',
    trigger: 'manual',
    selector: '#tag-tree-popover',
    content: $navTagTree
  })
}

function nodesCopy(selector, from, to){
  from.find(selector).children().clone().appendTo(to.find(selector).empty())
}

/*
 * common tip function
 */
function tipover($node, text, duration) {
    if (!duration) duration = 1000;
    
    if (!$node.data('bs.tooltip')) {
        $node.tooltip({placement: 'top', trigger: 'manual'});
    }
    $node.data('bs.tooltip').options.title = text;
    $node.tooltip('show');
    window.setTimeout(function(){$node.tooltip('hide');}, duration);
}

function commonConfirmPopover($node, action, message, placement) {
    var $block = $('<div>')
    $('<button class="btn">').text('是').appendTo($block).click(function(){
        action.apply($node)
        $node.popover('hide')
    })
    $('<button class="btn">').text('否').appendTo($block).click(function(){
        $node.popover('hide')
    })
    $node.popover({
        html: true,
        title: message,
        placement: placement,
        content: $block
    })
}

function limitStrLen(str, maxLen) {
  if (str.length > maxLen+3) {
    return str.substr(0, maxLen) + '...'
  } else {
    return str
  }
}

function parseNotifCount(type, data, lines) {
  var entry = data[type]
  if (entry) {
    lines.push(entry.count + '个' + entry.desc)
  }
}

function escapeHtml(str) {
  return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#39;')
}

function unescapeHtml(str) {
  return String(str).replace(/&amp;/g, '&').replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"').replace(/&#39;/g, '\'')
}

function textareaAutoResize() {
  var height = Math.max(this.scrollHeight, this.clientHeight)
  if (height > this.clientHeight) {
    $(this).css('height', height+'px')
  }
}
