function writeBlog_setup() {
  $('.btn-submit').tooltip({
    placement: 'top',
    trigger: 'manual'
  })
  $('#blog').submit(write_formSubmit)
  write_contentArea()
}

function writeTopic_setup() {
  $('.btn-submit').tooltip({
    placement: 'top',
    trigger: 'manual'
  })
  $('#topic').submit(write_formSubmit)
  write_contentArea()
}

function write_formSubmit() {
  try {
    var $form = $(this)
    var selectedTagIds = []
    $form.find('.tag-sel.btn-success').each(function (idx) {
      var tagId = parseInt($(this).attr('tag-id'))
      selectedTagIds.push(tagId)
    })

    var $submit = $form.find('.btn-submit')
    var $title = $('#title')
    if ($title.val().length == 0) {
      $title.fadeOut().fadeIn()
      tipover($submit, '请填写标题')
      return false
    }
    var $content = $('#content')
    if ($content.val().length == 0) {
      $content.fadeOut().fadeIn()
      tipover($submit, '请填写内容')
      return false
    }

    $submit.prop('disabled', true)
    $form.ajaxSubmit({
      data: {tagIds: selectedTagIds, draftId: window.draftId},
      success: redirect,
      error: function (msg) {
        var $submit = $form.find('.btn-submit')
        tipover($submit, '发表失败: ' + msg, 2000)
        $submit.prop('disabled', false)
      }
    })
  } catch (ex) {
    console.error(ex)
  } finally {
    return false
  }
}

function write_contentArea() {
  var $content = $('#content')
  $content.keydown(function(event){
    if (event.keyCode == 9) {
      event.preventDefault();
      var content = $(this).val();
      var pos = $(this).getCursorPosition();
      content = content.slice(0, pos) + '\t' + content.slice(pos, content.length);
      $(this).val(content);
      $(this).setCursorPosition(pos+1);
    }
  }).keyup(function() {
    write_contentRefresh();
  });
  $('#tabs a[href="#content"]').warnEmpty().tab('show');

  write_contentRefresh();
}

function write_contentRefresh() {
  var input = $('#content').val()
  $('#preview').html(marked(input))
}