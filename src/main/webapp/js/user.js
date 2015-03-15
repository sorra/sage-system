'use strict';
window.ucOpener;
window.ucCloser;

setupFollowDialog()


function launchUcOpener() {
	if (this == undefined) {
		console.log("launchUcOpener's this is undefined");
	}
	if (window.ucCloser && window.ucCloser.locator == this) {
		cancelUcCloser();
//		return;
	}

	cancelUcOpener();
	window.ucOpener = {
		timer : window.setTimeout($.proxy(openUserCard, this), 200),
		locator: this,
		uid: $(this).attr('uid')
	};
}

function launchUcCloser() {
	if (this == undefined) {
		console.log("launchUcCloser's this is undefined");
	}
	cancelUcOpener();

	cancelUcCloser();
	window.ucCloser = {
		timer: window.setTimeout(closeUserCard, 200),
		locator: this,
		uid: $(this).attr('uid')
	};
}

function openUserCard() {
	var target = this;
	cancelUcOpener();
	closeUserCard();
	console.log("open");
	$.get('/user/card/' + $(target).attr('uid'), {})
		.done(function(resp) {
			createPopupUserCard(target, resp).hide()
					.appendTo($('body')).fadeIn();
		})
		.fail(function(resp) {
			console.log('usercard Oops! ' + resp);
		});
}

function closeUserCard() {
	cancelUcCloser();
	console.log("close");
	var $uc = $('.user-card.popup:not(.proto *)');
	$uc.fadeOut('', function(){$uc.remove();});
}

function createPopupUserCard(target, card) {
	console.log("create");	
	var $uc = createUserCard(card).addClass('popup');
	if (window.userSelf && window.userSelf.id == card.id) {
		$('<button>').addClass('btn span').text('自己')
			.replaceAll($uc.find('.follow'));
	}
	var locatorPos = $(target).offset();
	var pleft = locatorPos.left;
	var ptop = locatorPos.top + $(target).height();
	$uc.css({
		position: 'absolute',
		left: pleft+'px', top: ptop+'px'
		});	
	$uc.mouseenter(cancelUcCloser).mouseleave($.proxy(launchUcCloser, target));

	return $uc;
}

function createUserCard(ucard) {
  var $uc = $(renderTmpl('tmpl-user-card', ucard))
	var $follow = $uc.find('.follow')
	if (ucard.isFollowing) {
		setAsFollowed($follow, ucard)
	} else {
		setAsNotFollowed($follow, ucard)
	}
	return $uc
}

function cancelUcOpener() {
	if (window.ucOpener) {
		window.clearTimeout(window.ucOpener.timer);
		window.ucOpener = null;
	}
}

function cancelUcCloser() {
	if (window.ucCloser) {
		window.clearTimeout(window.ucCloser.timer);
		window.ucCloser = null;
	}
}

function setAsFollowed($follow, uc) {
	$follow.text('已关注').addClass('btn btn-success span pull-right').click(function(){
		var $dialog = $('#follow-dialog')
		$dialog.data('usercard', uc)

		$('<button>').text('修改').addClass('btn btn-primary')
		.appendTo($dialog.find('.modal-footer').empty())
		.click(function(){
			var selectedTagIds = [];
			$dialog.find('.modal-body')
			.find('.btn-success').each(function(){
				var tagId = parseInt($(this).attr('tag-id'));
				selectedTagIds.push(tagId);
			});

			console.log("tagIds: " + selectedTagIds);
			$.post('/follow/'+uc.id, {tagIds: selectedTagIds})
			.fail(function(){
				alert('操作失败');
			});
				$dialog.modal('hide')
		});

		$('<button>').text('取消关注').addClass('btn btn-inverse')
		.appendTo($dialog.find('.modal-footer'))
		.click(function(){
			$.post('/unfollow/'+uc.id)
			.fail(function(){
				alert('操作失败');
			});
			$dialog.modal('hide')
		});

		$dialog.modal();
	});
}

function setAsNotFollowed($follow, uc) {
	$follow.text('关注').addClass('btn btn-success span pull-right').click(function(){
		var $dialog = $('#follow-dialog')
		$dialog.data('usercard', uc)

		$('<button>').text('关注').addClass('btn btn-primary')
		.appendTo($dialog.find('.modal-footer').empty())
		.click(function(){
			var selectedTagIds = [];
			$dialog.find('.modal-body')
			.find('.btn-success').each(function(){
				var tagId = parseInt($(this).attr('tag-id'));
				selectedTagIds.push(tagId);
			});

			console.log("tagIds: " + selectedTagIds);
			$.post('/follow/'+uc.id, {tagIds: selectedTagIds})
			.fail(function(){
				alert('操作失败');
			});
			$dialog.modal('hide')
		});

		$dialog.modal();
	});
}

function setupFollowDialog(){
	var $fdia = $(renderTmpl('tmpl-modal', {modalId: 'follow-dialog'})).appendTo($('body'))
	$fdia.find('.modal-title').text('请选择0~n个标签')
	$('#follow-dialog').on('show.bs.modal', function(){
		var $this = $(this)
		var $body = $this.find('.modal-body').empty()
		var uc = $this.data('usercard')
		if (!uc) {throw new Error}
		$.each(uc.tags, function(idx, item){
			var $tagBtn = $('<button>').text(item.name).attr('tag-id', item.id).addClass('btn').appendTo($body)
				.click(function(){
					$(this).toggleClass('btn-success');
				});
			console.info('followed tag ids: '+uc.followedTagIds);
			if ($.inArray(item.id, uc.followedTagIds) >= 0) {
				$tagBtn.addClass('btn-success');
			}
		})
	})
}