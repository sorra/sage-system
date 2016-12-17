function btnLike_init() {

  function refreshLikes($btn, prefix) {
    return $.get(prefix + '/likes').done(function(likes){
      if (likes.toString() == '0') {
        likes = ''
      }
      $btn.find('.num-likes').text(likes)
    })
  }

  $(document).on('click', '.like-btn', function(){
    var $btn = $(this)
    var prefix = $btn.data('prefix')
    var classLiked = 'like-btn-liked'
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