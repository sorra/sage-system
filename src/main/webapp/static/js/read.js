function btnLike_init() {

  function refreshLikes($btn, prefix) {
    return $.get(prefix + '/likes').done(function(likes){
      $btn.find('.num-likes').text(likes)
    })
  }

  $(document).on('click', '.btn_like', function(){
    var $btn = $(this)
    var prefix = $btn.data('prefix')
    var classLiked = 'btn-success'
    if ($btn.hasClass(classLiked)) {
      $.post(prefix + '/unlike').done(function(){
        refreshLikes($btn, prefix)
        $btn.removeClass(classLiked)
      }).fail(function(msg){
        tipover($btn, msg)
      })
    } else {
      $.post(prefix+'/like').done(function(){
        refreshLikes($btn, prefix)
        $btn.addClass(classLiked)
      }).fail(function(msg){
        tipover($btn, msg)
      })
    }
  })
}