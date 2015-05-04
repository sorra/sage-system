'use strict';

$(document).ready(function(){
  $('#nav-write-blog').addClass('active')

  tag_input_init()

	$('form.blog .btn[type=submit]')
    .tooltip({
      placement: 'top',
      trigger: 'manual'
    })
    .click(function(event){
      event.preventDefault()
      var $submit = $(this)
      $submit.prop('disabled', true)

      var selectedTagIds = []
      $('.tag-sel.btn-success').each(function(idx){
        var tagId = parseInt($(this).attr('tag-id'))
        selectedTagIds.push(tagId)
      })

      var blogId = window.frontMap.blogId;
      var submitUrl = blogId ? '/post/edit-blog/'+blogId : '/post/blog'
      var reqAttrs = {
        title: $('#title').val(),
        content: $('#content').val(),
        tagIds: selectedTagIds
      }
      if (window.frontMap.groupId) {
        reqAttrs.groupId = window.frontMap.groupId
      }
      $.post(submitUrl, reqAttrs)
        .always(function(r_){
          $submit.prop('disabled', false)
        })
        .done(function(resp){
          postBlogDone(resp)
        })
        .fail(function(err){
          postBlogFail(err)
        })
    })
})

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

function postBlogDone(url) {
  window.location = url
}

function postBlogFail(err) {
  var $submit = $('form.blog .btn[type=submit]')
  tipover($submit, '发表失败: '+err, 1000)
}

function refresh() {
  var input = $('#content').val()
  $('#preview').html(markdown.toHTML(input))
}