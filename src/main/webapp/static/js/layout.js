'use strict';

function buildNavTagTree($lnk, tagTree) {
  var $navTagTree = $('<div>')
  var $createTag = $('<button class="create-tag btn btn-warning">新建</button>').appendTo($navTagTree)

  var $dialog = $(renderTmpl("tmpl-modal", {modalId: 'new-tag-dialog'})).appendTo($('body'))
  var $nodes = $('#tmpl-new-tag-dialog')
  nodesCopy('.modal-title', $nodes, $dialog)
  nodesCopy('.modal-body', $nodes, $dialog)
  nodesCopy('.modal-footer', $nodes, $dialog)

  $dialog.find('.submit').click(function() {
    var name = $dialog.find('#name').val()
    var parentId = $dialog.find('#parent-id').val()
    if (name && name.length > 0 && parentId && parentId.length > 0) {
      $.post('/tag/new', {
        name: name,
        parentId: parentId
      })
    }
    $dialog.find('#name').val('')
    $dialog.find('#parent-id').val('')
    $dialog.modal('hide')
  })

  $('body').delegate('.create-tag', 'click', function(){
    $dialog.modal('show')
  })

  tag_tree(tagTree).appendTo($navTagTree)

  $('body').delegate('.tag-tree .tag-label', 'click', function(){
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

function userLinkAttrs(id) {
  return {uid: id, href: '/private/'+id}
}
function showTime(time) {
  return new Date(parseInt(time)).toLocaleString()
}

function renderTmpl(tmplId, object) {
  if (!object || object == "") {
    throw new Error
  }
  return template(tmplId, object)
}
template.helper('showTime', showTime)
template.helper('userLinkAttrs', function (id){
  return 'href="'+'/private/'+id+'" uid="'+id+'"'
})
template.helper('global', function(){return window})

$(document).ready(function(){
  if ($('#front-map').length > 0) {
    window.frontMap = $.parseJSON($('#front-map').text());
  } else {
    window.frontMap = {};
  }

  if ($('#user-self-json').length > 0) {
      window.userSelf = $.parseJSON($('#user-self-json').text());
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

  $.get("/notif/unread-counts").done(function(data){
    var notifCountLines = []
    parseNotifCount('FORWARDED', data, notifCountLines)
    parseNotifCount('COMMENTED', data, notifCountLines)
    parseNotifCount('REPLIED', data, notifCountLines)
    parseNotifCount('MENTIONED_TWEET', data, notifCountLines)
    parseNotifCount('MENTIONED_COMMENT', data, notifCountLines)
    parseNotifCount('FOLLOWED', data, notifCountLines)
    var notifCountHtml = ''
    for (var i in notifCountLines) {
      notifCountHtml += '<a href="/pages/notif/unread" style="display: block">'+notifCountLines[i]+'</a>'
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
});

function parseNotifCount(type, data, lines) {
  var entry = data[type]
  if (entry) {
    lines.push(entry.count + '个' + entry.desc)
  }
}

function escapeHtml(str) {
  return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}

function unescapeHtml(str) {
  return String(str).replace(/&amp;/g, '&').replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"').replace(/&#39;/g, '\'');
};

function textareaAutoResize() {
  var height = Math.max(this.scrollHeight, this.clientHeight);
  if (height > this.clientHeight) {
    $(this).css('height', height+'px');
  }
}
