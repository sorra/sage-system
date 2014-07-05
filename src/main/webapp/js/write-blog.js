'use strict';

$(document).ready(function(){
  $('#nav-write-blog').addClass('active');

  var existingTags = window.frontMap.existingTags;
  var topTags = window.userSelf.topTags;
  if (existingTags) {
    buildTagSels(existingTags);
    $('.tag-sel').addClass('btn-success');
    var tmpTags = [];
    for (var _i in topTags) {
      var noSame = true;
      for (var _j in existingTags) {
        if (topTags[_i].id == existingTags[_j].id) {
          noSame = false; break;
        }
      }
      if (noSame) {tmpTags.push(topTags[_i])}
    }
    topTags = tmpTags;
  }
	buildTagSels(topTags);
	buildTagPlus();

	$('form.blog .btn[type=submit]')
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
			console.log('fff');
		});

    var blogId = window.frontMap.blogId;
		var submitUrl = webroot + (blogId ? '/post/edit-blog/'+blogId : '/post/blog');
		$.post(submitUrl, {
			title: $('.blog .title').val(),
			content: $('.blog .content').val(),
			tagIds: selectedTagIds
		})
		.always(function(resp){
			$submit.prop('disabled', false);
		})
		.done(function(resp){
			if ($.isNumeric(resp) && resp >= 0) postBlogDone(resp);
			else postBlogFail(resp);
		})
		.fail(function(resp){
			postBlogFail(resp);
		});
	});
});

$(document).ready(function() {
    $('#content')
    .keydown(function(event){
        if (event.keyCode == 9) {
            event.preventDefault();
            var content = $(this).val();
            var pos = $(this).getCursorPosition();
            content = content.slice(0, pos) + '\t' + content.slice(pos, content.length);
            $(this).val(content);
            $(this).setCursorPosition(pos+1);
        }
    })
    .keyup(function() {
        textareaAutoResize.apply(this);
        refresh();
    });
    $('#tabs a[href="#content"]').warnEmpty().tab('show');
    
    var $content = $('#content');
    //TODO is this OK?
    $content.val(unescapeHtml($content.val()));
    
    refresh();
});

function postBlogDone(blogId) {
	var $submit = $('form.blog .btn[type=submit]');
    tipover($submit, '发表成功', 1000);
    setTimeout(function(){window.location = webroot + '/blog/'+blogId}, 1000);
}

function postBlogFail(resp) {
    console.warn("Post-blog-fail: " + resp);
	var $submit = $('form.blog .btn[type=submit]');
    tipover($submit, '发表失败', 1000);
}

function refresh() {
    console.log('refresh');
    var input = $('#content').val();
    $('#preview').html(markdown.toHTML(input));
}