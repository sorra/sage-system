'use strict';

$(document).ready(function(){
  $('#nav-home').addClass('active');
  
  var $selfCard = createUserCard(window.userSelf).css('border', '0').css('border-radius', '0');
  $selfCard.find('.follow').remove();
  $selfCard.appendTo($('.self-card'));
  
  var friends = window.frontMap.friends;
  var $friends = $('.friends');
  for (var _i in friends) {
      var $li = $('<li>');
      createUserLabel(friends[_i]).appendTo($li);
      $li.appendTo($friends);
  }
  $friends.appendTo($('.friends'));
    
	buildTagSels(window.userSelf.topTags);
	buildTagPlus();

	// prepare tweet-submit-button
	$('form.top-box .btn[type="submit"]')
	.tooltip({
		placement: 'top',
		trigger: 'manual'
	})
	.click(function(event){
		event.preventDefault();
		var $submit = $(this);
		$submit.prop('disabled', true);

		var selectedTagIds = [];
		$('.tag-sel.btn-success').each(function(idx){
			var tagId = parseInt($(this).attr('tag-id'));
			selectedTagIds.push(tagId);
			$(this).removeClass('.btn-success');
		});

		var input = $('form.top-box .input').val();
		if (input.trim().length == 0) {
			postTweetFail();
			$submit.prop('disabled', false);
			return;
		} 

		$.post('/post/tweet', {
			content: input,
			tagIds: selectedTagIds
		})
		.always(function(resp){
			$submit.prop('disabled', false);
		})
		.done(function(resp){
			console.log(resp);
			if (resp == true) postTweetDone();
			else postTweetFail();
		})
		.fail(function(resp){
			postTweetFail();
		});
	});

	// load istream
	getStream('/read/istream')
});

function postTweetDone() {
	var $submit = $('form.top-box .btn[type="submit"]');
	$('form.top-box .input').val('');
	tipover($submit, '发表成功', 1000);
}

function postTweetFail() {
	var $submit = $('form.top-box .btn[type="submit"]');
	tipover($submit, '发表失败', 1000);
}

function createUserLabel(ulabel) {
  var $ulb = $(renderTmpl('tmpl-user-label', ulabel))
  $ulb.find('a[uid]').mouseenter(launchUcOpener).mouseleave(launchUcCloser);
  return $ulb;
}