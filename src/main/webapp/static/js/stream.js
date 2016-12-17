var streamModel = {
  url: '',
  ajaxMark: null
}

function stream_setup() {
  setupForwardDialog()
  stream_setupListeners()
  btnLike_init()

  $('.stream .newfeed').click(funcLookNewer())
  var lookEarlier = funcLookEarlier()
  $('.stream .oldfeed').click(lookEarlier)
  $(window).scroll(function () {
    var scrollTop = $(window).scrollTop()
    var winHeight = $(window).height()
    var docuHeight = $(document).height()
    if (scrollTop + winHeight >= docuHeight) {
      lookEarlier()
      console.info(scrollTop + ' ' + winHeight + ' ' +docuHeight)
    }
  })
}

function getStream() {
  $('.stream-items').tipover('获取中···', 3000)
  return $.get(window.streamModel.url)
    .done(function (resp) {
      if (resp) {
        $('.stream-items').empty().append($(resp))
        humanTime_show()
        $('.stream-items').tipover('获取了' + $('.stream-item').length + '条信息')
      } else {
        $('.stream-items').tipover('信息流为空')
      }
    })
    .fail(function (resp) {
      $('.stream-items').tipover(resp.errorMsg || '网络错误');
    })
}

function funcLookNewer(ignoreEmpty) {
  return function() {
    var largest = null
    $('.stream-items .tweet').each(function () {
      var id = parseInt($(this).attr('tweet-id'))
      if (id && (largest == null || id > largest)) {
        largest = id
      }
    })
    console.info("largest " + largest)

    var ajaxMark = new Object
    window.streamModel.ajaxMark = ajaxMark
    return $.get(window.streamModel.url, {after: largest}).done(function (resp) {
      if (ajaxMark == window.streamModel.ajaxMark) {
        var $items = resp ? $(resp) : $()
        if ($items.length > 0) {
          $('.stream-items').prepend($(resp))
        } else if (!ignoreEmpty) {
          $('.stream .newfeed').tipover('还没有新的')
        }
        humanTime_show()
      }
    }).fail(function (resp) {
      $('.stream .newfeed').tipover(resp.errorMsg || '网络错误')
    })
  }
}

function funcLookEarlier(ignoreEmpty) {
  return function() {
    var smallest = null
    $('.stream-items .tweet').each(function () {
      var id = parseInt($(this).attr('tweet-id'))
      if (id && (smallest == null || id < smallest)) {
        smallest = id
      }
    })
    console.info("smallest " + smallest)

    var ajaxMark = new Object
    window.streamModel.ajaxMark = ajaxMark
    return $.get(window.streamModel.url, {before: smallest}).done(function (resp) {
      if (ajaxMark == window.streamModel.ajaxMark) {
        var $items = resp ? $(resp) : $()
        if ($items.length > 0) {
          $('.stream-items').append($(resp))
        } else if (!ignoreEmpty) {
          $('.stream .oldfeed').tipover('没有更早的了')
        }
        humanTime_show()
      }
    }).fail(function (resp) {
      $('.stream .old-feed').tipover(resp.errorMsg || '网络错误')
    })
  }
}

function deleteDialogEach() {
  var $tweet = $(this).parents('.tweet').warnEmpty()
  var tweetId = $tweet.attr('tweet-id')
  function doDelete(id){
    if (!id) {
      console.warn('this id is '+id)
      return
    }
    $.post('/tweets/'+id+'/delete')
      .done(function(resp){
        if(resp == true) {
          if ($tweet.hasClass('t-forward')) {
            var $combine = $tweet.parents('.combine')
            $tweet.remove()
            if (!$combine.data('containsOrigin') && $combine.find('.t-forward').length == 0) {
              $combine.remove()
            }
          } else {
            $tweet.remove()
          }
        }
        else {console.error("Tweet "+id+" delete failed.")}
      })
      .fail(function(resp){console.error("Tweet "+id+" delete failed. Error: "+resp)})
  }
  commonConfirmPopover($(this), function(){doDelete(tweetId)}, '确认要删除吗？', 'left')
}

function setupForwardDialog() {
  var $dia = $('#forward-dialog')
  $dia.find('.modal-title').text('转发')
  $dia.on('show.bs.modal', function(){
    var $tweet = $dia.data('tweet')
    var $midForwards = $dia.find('.mid-forwards').empty()
    $tweet.find('.tweet-self-body[data-id='+$tweet.attr('tweet-id')+'] *[mf-id]').clone().appendTo($midForwards).show()
    $midForwards.children().each(function(){
      $(this).append('<a class="mf-x" href="javascript:;">&times;</a>')
    }).find('a[uid]').removeAttr('href')
    $dia.find('.modal-body .input').val('')
  })
}

function stream_setupListeners() {
  var $doc = $(document)
  $doc.delegate('a[uid]', 'mouseenter', launchUcOpener).delegate('a[uid]', 'mouseleave', launchUcCloser)
  $doc.delegate('.tweet-ops .forward-btn', 'click', function() {
    var $tweet = $(this).parents('.tweet').warnEmpty()
    $('#forward-dialog').data('tweet', $tweet).modal({backdrop: false})
  })
  $doc.delegate('.tweet-ops .comment-btn', 'click', toggleTweetComments)
  //TODO deleteDialogEach is not done yet
  $doc.delegate('.tweet-ops .delete-btn', 'click', deleteDialogEach)


  $doc.delegate('#forward-dialog .mf-x', 'click', function() {
    $(this).parents('*[mf-id]').addClass('mf-removed')
  })
  $doc.delegate('#forward-dialog .btn-primary', 'click', function() {
    var $dialog = $('#forward-dialog')
    $.post('/post/forward', {
      content: $dialog.find('.input').val(),
      originId: $dialog.data('tweet').attr('tweet-id'),
      removedIds: $dialog.find('.mf-removed').map(function() {
        return $(this).attr('mf-id')
      }).get()
    }).done(funcLookNewer('/stream/i'))
    $dialog.modal('hide')
  })

  $doc.delegate('.tweet-content .view-img', 'click', function(){
    $(this).toggleClass('view-large')
  })
}
