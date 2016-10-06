var stream = {}

function stream_setup() {
  setupForwardDialog()
  stream_setupListeners()
}

function getStream(url) {
  return $.get(url)
    .done(function (resp) {
      if (resp) createStream(resp, url)
      else tipover($('.slist'), '信息流为空')
    })
    .fail(function (resp) {
      tipover($('.slist'), '信息流加载失败: ' + resp);
    });
}

function getStreamAfter(url, afterId, callback) {
  if (!afterId) {
    console.error('afterId is ' + afterId)
  }
  var ajaxMark = new Object
  window.stream.ajaxMark = ajaxMark
  return $.get(url, {after: afterId})
    .done(function (resp) {
      if (resp && ajaxMark == window.stream.ajaxMark) {
        $('.slist').prepend($(resp))
        humanTime_show()
      }
      callback(resp)
    })
    .fail(function (resp) {
      tipover($('.slist'), '信息流向后加载失败: ' + resp)
    });
}

function getStreamBefore(url, beforeId, callback) {
  if (!beforeId) {
    console.error('beforeId is ' + beforeId)
  }
  var ajaxMark = new Object
  window.stream.ajaxMark = ajaxMark
  return $.get(url, {before: beforeId})
    .done(function (resp) {
      if (resp && ajaxMark == window.stream.ajaxMark) {
        $('.slist').append($(resp))
        humanTime_show()
      }
      callback(resp)
    })
    .fail(function (resp) {
      tipover($('.slist'), '信息流向前加载失败: ' + resp)
    });
}

function funcLookNewer(url, callback) {
  return function() {
    var largest = null
    $('.slist .tweet').each(function () {
      var id = parseInt($(this).attr('tweet-id'))
      if (id && (largest == null || id > largest)) {
        largest = id
      }
    })
    console.info("largest " + largest)
    getStreamAfter(url, largest, callback)
  }
}

function funcLookEarlier(url, callback) {
  return function() {
    var smallest = null
    $('.slist .tweet').each(function () {
      var id = parseInt($(this).attr('tweet-id'))
      if (id && (smallest == null || id < smallest)) {
        smallest = id
      }
    })
    console.info("smallest " + smallest)
    getStreamBefore(url, smallest, callback)
  }
}

function createStream(resp, url) {
  var $stream = $('.stream')
  $('.slist').empty().append($(resp))
  humanTime_show()

  $('<a class="newfeed btn">').text('看看新的').prependTo($stream).click(funcLookNewer(url, function(resp){
    if (!resp) {
      tipover($('.stream .newfeed').warnEmpty(), '还没有新的')
    }
  }))

  var lookEarlier = funcLookEarlier(url, function(resp){
    if (!resp) {
      tipover($('.stream .oldfeed').warnEmpty(), '没有更早的了')
    }
  })
  $('<a class="oldfeed btn">').text('看看更早的').appendTo($stream).click(lookEarlier)
  $(window).scroll(function(){
    var scrollTop = $(window).scrollTop()
    var winHeight = $(window).height()
    var docuHeight = $(document).height()
    if (scrollTop + winHeight >= docuHeight) {
      lookEarlier()
      console.info(scrollTop + ' ' + winHeight + ' ' +docuHeight)
    }
  })
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
    $tweet.find("*[mf-id]").clone().appendTo($midForwards).show()
    $midForwards.children().each(function(){
      $(this).append('<a class="mf-x" href="javascript:;">&times;</a>')
    }).find('a[uid]').removeAttr('href')
    $dia.find('.modal-body .input').val('')
  })
}

function stream_setupListeners() {
  var $doc = $(document)
  $doc.delegate('a[uid]', 'mouseenter', launchUcOpener).delegate('a[uid]', 'mouseleave', launchUcCloser)
  $doc.delegate('.tweet-ops .forward', 'click', function() {
    var $tweet = $(this).parents('.tweet').warnEmpty()
    $('#forward-dialog').data('tweet', $tweet).modal({backdrop: false})
  })
  $doc.delegate('.tweet-ops .comment', 'click', toggleTweetComments)
  //TODO deleteDialogEach is not done yet
  $doc.delegate('.tweet-ops .delete', 'click', deleteDialogEach)


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
