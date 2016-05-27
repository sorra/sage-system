'use strict';

function user_setup() {
  window.ucOpener = null
  window.ucCloser = null
  setupFollowDialog()
  user_setupListeners()
}

function launchUcOpener() {
  if (this == undefined) {
    console.log("launchUcOpener's this is undefined");
  }
  if (window.ucCloser && window.ucCloser.locator == this) {
    cancelUcCloser()
    // return
  }
  cancelUcOpener()
  window.ucOpener = {
    timer: window.setTimeout($.proxy(openUserCard, this), 200),
    locator: this,
    uid: $(this).attr('uid')
  }
}

function launchUcCloser() {
  if (this == undefined) {
    console.log("launchUcCloser's this is undefined")
  }
  cancelUcOpener()
  cancelUcCloser()
  window.ucCloser = {
    timer: window.setTimeout(closeUserCard, 200),
    locator: this,
    uid: $(this).attr('uid')
  }
}

function openUserCard() {
  var target = this
  cancelUcOpener()
  closeUserCard()
  console.log("openUserCard")
  var url = '/users/' + $(target).attr('uid') + '/card'
  $.get(url, {}).done(function(resp) {
      createPopupUserCard(target, resp).hide()
        .appendTo($('body')).fadeIn()
    }).fail(function(resp) {
      console.log(url + ' Oops! ' + resp)
    })
}

function closeUserCard() {
  cancelUcCloser()
  console.log("close")
  var $uc = $('.user-card.popup')
  $uc.fadeOut('', function(){$uc.remove()})
}

function createPopupUserCard(target, resp) {
  console.log("createPopupUserCard")
  var $uc = $(resp).addClass('popup')
  var locatorPos = $(target).offset()
  var pleft = locatorPos.left
  var ptop = locatorPos.top + $(target).height()
  $uc.css({
    position: 'absolute',
    left: pleft+'px', top: ptop+'px'
  })
  $uc.mouseenter(cancelUcCloser).mouseleave($.proxy(launchUcCloser, target))
  return $uc
}

function cancelUcOpener() {
  if (window.ucOpener) {
    window.clearTimeout(window.ucOpener.timer)
    window.ucOpener = null
  }
}

function cancelUcCloser() {
  if (window.ucCloser) {
    window.clearTimeout(window.ucCloser.timer)
    window.ucCloser = null
  }
}

function setupFollowDialog(){
  var $dia = $('#follow-dialog')
  $dia.on('show.bs.modal', function(){
    var $this = $(this)
    var $body = $this.find('.modal-body').empty()
    var uc = $this.data('usercard')
    if (!uc) {throw new Error}
    $.each(uc.tags, function(idx, item){
      var $tagBtn = $('<button>').text(item.name).attr('tag-id', item.id).addClass('btn').appendTo($body)
        .click(function(){
          $(this).toggleClass('btn-success');
        });
      if (uc.follow) {
        console.info('followed tag ids: '+uc.follow.tagIds);
        if ($.inArray(item.id, uc.follow.tagIds) >= 0) {
          $tagBtn.addClass('btn-success');
        }
      }
    })
    var $inew = $('<div><input name="includeNew" type="checkbox"/>自动订阅新标签</div>')
    if (uc.follow && uc.follow.includeNew) $inew.find('input').prop('checked', true)
    $inew.appendTo($body)
    var $iall = $('<div><input name="includeAll" type="checkbox"/>全订阅，不过滤</div>')
    if (uc.follow && uc.follow.includeAll) $iall.find('input').prop('checked', true)
    $iall.appendTo($body)
  })
}

function user_setupListeners() {
  var $docu = $(document)

  $docu.delegate('#follow-dialog .btn_follow', 'click', function(){
    var $dialog = $('#follow-dialog')
    var uc = $dialog.data('usercard')
    var selectedTagIds = []
    $dialog.find('.modal-body')
        .find('.btn-success').each(function(){
          var tagId = parseInt($(this).attr('tag-id'))
          selectedTagIds.push(tagId)
        })
    var includeNew = $dialog.find('input[name=includeNew]').is(':checked')
    var includeAll = $dialog.find('input[name=includeAll]').is(':checked')

    console.log("tagIds: " + selectedTagIds)
    $.post('/follow/'+uc.id, {tagIds: selectedTagIds, includeNew: includeNew, includeAll: includeAll})
      .fail(function(){alert('操作遇到问题')})
    $dialog.modal('hide')
  })

  $docu.delegate('#follow-dialog .btn_editfollow', 'click', function(){
    var $dialog = $('#follow-dialog')
    var uc = $dialog.data('usercard')
    var selectedTagIds = []
    $dialog.find('.modal-body')
        .find('.btn-success').each(function(){
          var tagId = parseInt($(this).attr('tag-id'));
          selectedTagIds.push(tagId)
        })
    var includeNew = $dialog.find('input[name=includeNew]').is(':checked')
    var includeAll = $dialog.find('input[name=includeAll]').is(':checked')

    console.log("tagIds: " + selectedTagIds)
    $.post('/follow/'+uc.id, {tagIds: selectedTagIds, includeNew: includeNew, includeAll: includeAll})
        .fail(function(){alert('操作遇到问题')})
    $dialog.modal('hide')
  })

  $docu.delegate('#follow-dialog .btn_unfollow', 'click', function(){
    var $dialog = $('#follow-dialog')
    var uc = $dialog.data('usercard')
    $.post('/unfollow/'+uc.id)
        .fail(function(){alert('操作失败')})
    $dialog.modal('hide')
  })

  $docu.delegate('.user-card .follow', 'click', function(){
    var isFollowing = $(this).text() != '+关注'
    var $dialog = $('#follow-dialog').data('usercard', $(this).parents('.user-card').data('usercard'))
    if (isFollowing){
      $('<button>').text('修改').addClass('btn_editfollow btn btn-primary')
          .appendTo($dialog.find('.modal-footer').empty())
      $('<button>').text('取消关注').addClass('btn_unfollow btn btn-inverse')
          .appendTo($dialog.find('.modal-footer'))
      $dialog.modal()
    } else {
      $('<button>').text('关注').addClass('btn_follow btn btn-primary')
        .appendTo($dialog.find('.modal-footer').empty())
      $dialog.modal()
    }
  })

  $docu.delegate('.user-card .btn_message', 'click', function(){
    window.open($(this).data('url'))
  })
}