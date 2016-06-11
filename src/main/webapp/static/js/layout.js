'use strict';

$(document).ready(function(){
  $(document).on('focus', 'a, button, input[type=submit]', function(){this.blur()})

  if ($('#front-map').length > 0) {
    window.frontMap = $.parseJSON($('#front-map').text());
  } else {
    window.frontMap = {};
  }

  if ($('#user-self-json').length > 0) {
    window.userSelf = $.parseJSON($('#user-self-json').text());

    var uri = window.location.toString()
    var idxOfQueMark = uri.indexOf('?')
    if(idxOfQueMark >= 0) uri = uri.substring(0, idxOfQueMark)
    if (uri.indexOf('/notifications') < 0) {
      $.get("/notifications/unread-counts").done(function (data) {
        var notifCountLines = []
        parseNotifCount('FORWARDED', data, notifCountLines)
        parseNotifCount('COMMENTED', data, notifCountLines)
        parseNotifCount('REPLIED', data, notifCountLines)
        parseNotifCount('MENTIONED_TWEET', data, notifCountLines)
        parseNotifCount('MENTIONED_COMMENT', data, notifCountLines)
        parseNotifCount('FOLLOWED', data, notifCountLines)
        var notifCountHtml = ''
        for (var i in notifCountLines) {
          notifCountHtml += '<a href="/notifications/unread" style="display: block">' + notifCountLines[i] + '</a>'
        }
        if (notifCountHtml.length > 0) {
          $('#nav-user').popover({
            html: true,
            placement: 'bottom',
            trigger: 'manual',
            selector: '#notif-count-popover',
            container: 'body',
            content: notifCountHtml
          }).popover('show')
        }
      })
    }
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

  var $parentInput = $('#new-tag-dialog').find('input[name=parentId]')

  $parentInput.each(tagCompleteInitFunc(function(tag){
    this.value = tag.id
  }))
  $parentInput.on('input', tagCompleteHandlerOnInput)

  $(document).delegate('#new-tag-dialog .submit', 'click', function() {
    var $dialog = $('#new-tag-dialog')
    var name = $dialog.find('input[name=name]').val()
    var parentId = $dialog.find('input[name=parentId]').val()
    var isCore = $dialog.find('input[name=isCore]')[0].checked
    if (name && name.length > 0) {
      var attrs = {name: name}
      if (parentId.length > 0) attrs.parentId = parentId
      if (isCore) attrs.isCore = isCore
      $.post('/tags/new', attrs)
    }
    $dialog.find('input[name=name]').val('')
    $dialog.find('input[name=parentId]').val('')
    $dialog.modal('hide')
  })

  $(document).delegate('.create-tag', 'click', function(){
    if (window.userSelf) {
      $('#new-tag-dialog').modal('show')
    } else {
      tipover($(this), '需要登录')
    }
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

function textareaAutoResize() {
  var height = Math.max(this.scrollHeight, this.clientHeight)
  if (height > this.clientHeight) {
    $(this).css('height', height+'px')
  }
}
