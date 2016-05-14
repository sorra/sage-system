$('#form input[type=submit]').click(function(e){
  e.preventDefault()
  var $submit = $(this)

  var selectedTagIds = []
  $('.tag-sel.btn-success').each(function(idx){
    var tagId = parseInt($(this).attr('tag-id'))
    selectedTagIds.push(tagId)
  })

  $.post($('#form').attr('action'), {
    name: $('#form *[name=name]').val(),
    introduction: $('#form *[name=introduction]').val(),
    tagIds: selectedTagIds
  }).done(function(url){
    window.location = url
  }).fail(function(err){
    tipover($submit, '操作失败: '+err, 1000);
  })
})