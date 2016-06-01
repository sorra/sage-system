function btnLike_init(resUrl) {

  function updateLikes($btn) {
    return $.get(resUrl + '/likes').done(function(likes){
      $btn.find('.num-likes').text(likes)
    })
  }

  $('.btn_like').click(function(){
    var $btn = $(this)
    var classLiked = 'btn-success'
    if ($btn.hasClass(classLiked)) {
      $.post(resUrl + '/unlike').done(function(){
        updateLikes($btn)
        $btn.removeClass(classLiked)
      }).fail(function(msg){
        tipover($btn, msg)
      })
    } else {
      $.post(resUrl+'/like').done(function(){
        updateLikes($btn)
        $btn.addClass(classLiked)
      }).fail(function(msg){
        tipover($btn, msg)
      })
    }
  })
}