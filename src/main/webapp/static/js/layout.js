$(document).ready(function(){
  $(document).on('focus', 'a, button, input[type=submit]', function(){this.blur()})

  if ($('#front-map').length > 0) {
    window.frontMap = $.parseJSON($('#front-map').text());
    window.userSelf = window.frontMap.userSelf
    window.tagTree = window.frontMap.tagTree
  } else {
    window.frontMap = {};
  }

  if (window.userSelf) {
    var uri = window.location.toString()
    var idxOfQueMark = uri.indexOf('?')
    if(idxOfQueMark >= 0) uri = uri.substring(0, idxOfQueMark)
    if (uri.indexOf('/notifications') < 0) {
      fetchNotifications()
    }
  }

  if (window.tagTree) {
    var $lnk = $('#nav-tags')
      .click(function(event){
        event.preventDefault();
        $(this).popover('toggle');
      });
    buildNavTagTree($lnk, window.tagTree);
  }

  var isBackToTopHidden = true
  window.onscroll = function(){
        var toTop = document.body.scrollTop || document.documentElement.scrollTop
        if (toTop > 0 && isBackToTopHidden) {
            $('#back-to-top').css('display', 'block')
            isBackToTopHidden = false
        } else if (toTop == 0 && !isBackToTopHidden) {
            $('#back-to-top').css('display', 'none')
            isBackToTopHidden = true
        };
    }
  $('#back-to-top').click(function(){
    document.documentElement.scrollTop = document.body.scrollTop = 0
  })
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
      //TODO check login via AJAX
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
    container: 'body',
    content: $navTagTree
  })
}

function nodesCopy(selector, from, to){
  from.find(selector).children().clone().appendTo(to.find(selector).empty())
}

function fetchNotifications() {
  $.get("/notifications/unread-counts").done(function (data) {
    var notifCountLines = []
    parseNotifCounts(data, notifCountLines)
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
  }).always(function () {
    setTimeout(fetchNotifications, 3000)
  })
}

function parseNotifCounts(data, lines) {
  for (var attr in data) {
    var entry = data[attr]
    if (data.hasOwnProperty(attr) && entry.count && entry.desc) {
    lines.push(entry.count + '个' + entry.desc)
    }
  }
}