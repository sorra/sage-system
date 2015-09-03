'use strict';

template.helper('replaceMention', replaceMention)
template.helper('reduceMention', reduceMention)
template.helper('knowDeleted', function(content){
  if (!content || content.length == 0) return '[已删除]'
  else return content
})
template.helper('showCount', function(count){
  return count>0 ? '('+count+')' : ''
})
template.helper('asOrigin', function(origin){
  origin.isOrigin = true; return origin
})
template.helper('asForward', function(forward){
  forward.isForward = true; return forward
})
template.helper('toFw', function(t){
  return '@' + t.authorName + '#' + t.authorId + ' : ' + t.content
})

var tweetCache = {
  prefix: 'tweet-',
  set: function(t){
    $(document).data(this.prefix + t.id, t)
  },
  get: function(id){
    return $(document).data(this.prefix + id)
  },
  getNonNull: function(id){
    var t = this.get(id)
    if (!t) {throw new Error}
    return t
  },
  getOrEmpty: function(id){
    var t = this.get(id)
    return t ? t : {}
  }
}

setupForwardDialog()
stream_setupListeners()

$(document).ready(function() {
  $(document).on('click', '#forward-dialog .mf-x', function() {
    $(this).parents('*[mf-id]').addClass('mf-removed')
  })
})

function getStream(url) {
    return $.get(url, {})
        .done(function(resp){
            if (resp == null) alert('stream is null');
            else createStream(resp, url);
        })
        .fail(function(resp){
            tipover($('.slist'), 'stream Oops! ' + resp);
        });
}

function getStreamAfter(url, afterId, callback) {
    if (afterId == undefined) throw new Error;
    return $.get(url, {after: afterId})
        .done(function(resp){
            if (resp == null) console.error('stream is null');
            else {
              createStreamAfter(resp, url);
              if (callback) callback(resp)
            }
        })
        .fail(function(resp){
            console.error('getStreamAfter Oops! ' + resp);
        });
}

function getStreamBefore(url, beforeId, callback) {
    if (beforeId == undefined) throw new Error;
    return $.get(url, {before: beforeId})
        .done(function(resp){
            if (resp == null) console.error('stream is null');
            else {
              createStreamBefore(resp, url);
              if (callback) callback(resp)
            }
        })
        .fail(function(resp){
            console.error('getStreamBefore Oops! ' + resp);
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

function createStream(stream, url) {
  var $stream = $('.stream')
  var $slist = $('.slist').empty().warnEmpty()
  $.each(stream.items, function(idx, item){
    if (item.type == 'TweetView') {
      createTweetCard(item).appendTo($slist)
    }
    else if (item.type == 'CombineGroup') {
      createCombineGroup(item).appendTo($slist)
    }
  })

  $('<a class="newfeed btn">').text('看看新的').prependTo($stream).click(funcLookNewer(url, function(stream){
    if (stream.items.length == 0) {
      tipover($('.stream .newfeed').warnEmpty(), '还没有新的')
    }
  }))

  var lookEarlier = funcLookEarlier(url, function(stream){
    if (stream.items.length ==0) {
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

function createStreamAfter(stream) {
  var $slist = $('.slist');
  $.each(stream.items.reverse(), function(idx, item){
    createStreamItem(item).prependTo($slist)
  });
}

function createStreamBefore(stream) {
  var $slist = $('.slist')
  $.each(stream.items, function(idx, item){
    createStreamItem(item).appendTo($slist)
  });
}

function createStreamItem(item) {
  if (item.type == 'TweetView') {return createTweetCard(item)}
  else if (item.type == 'CombineGroup') {return createCombineGroup(item)}
}

function createTweetCard(tweet) {
  tweetCache.set(tweet)
  if(tweet.origin) {tweetCache.set(tweet.origin)}
  var $tc = $(renderTmpl('tmpl-tweet', tweet))
  enchantTweets($tc)
  return $tc;
}

function createCombineGroup(group) {
  tweetCache.set(group.origin)
  for(var i in group.forwards) {
    tweetCache.set(group.forwards[i])
  }
  var $cg = $(renderTmpl('tmpl-combine', group))
  enchantTweets($cg)
  return $cg
}

function enchantTweets($elem) {
  $elem.find('a[uid]').mouseenter(launchUcOpener).mouseleave(launchUcCloser)
  $elem.find('.tweet-ops .delete').each(deleteDialogEach)
  $elem.find('.tweet-ops .comment').click(commentDialog)
}

function deleteDialogEach() {
  var $tweet = $(this).parents('.tweet').warnEmpty()
  var tweetId = $tweet.attr('tweet-id')
  function doDelete(id){
    if (!id) {
      console.warn('this id is '+id)
      return
    }
    $.post('/tweet/'+id+'/delete')
      .done(function(resp){
        if(resp == true) {$tweet.remove()}
        else {console.error("Tweet "+id+" delete failed.")}
      })
      .fail(function(resp){console.error("Tweet "+id+" delete failed. Error: "+resp)})
  }
  commonConfirmPopover($(this), function(){doDelete(tweetId)}, '确认要删除吗？', 'left')
}

function commentDialog(){
  var $this = $(this)
  var $tc = $this.parents('.tweet')
  var tweetId = $tc.attr('tweet-id')
  var clKey = 'comment-list'
  var $cl = $this.data(clKey)

  if ($cl) {
    $cl.remove()
    $this.removeData(clKey)
  } else {
    var retach = function($commentList){
      var $clOld = $this.data(clKey)
      if ($clOld) {
        $clOld.remove()
      }
      var $clNew = $commentList.appendTo($tc.find('.tweet-body'))
      $this.data(clKey, $clNew)
    }
    retach(createCommentList(tweetId, retach))
  }
}

function createCommentList(tweetId, retach) {
  var $cl = $('#tmpl-comment-dialog').children().clone()
  var $input = $cl.find('textarea').on('keyup', textareaAutoResize)
  $cl.find('.btn-success').click(function(){
     $.post('/post/comment', {
       content: $input.val(), sourceId: tweetId
     }).success(function(){
       console.info('Post comment success, retach the list.')
       retach(createCommentList(tweetId, retach))
     })
     $input.val('')
  })
  var $loading = $cl.find('.loading')
  var $list = $cl.find('.comment-list')

  $.get('/read/'+tweetId+'/comments')
    .done(function(resp){
      $.each(resp, function(idx, item){
        $(renderTmpl('tmpl-tweet-comment', item)).appendTo($list)
      })
      $loading.text('评论')
    })
    .fail(function(){
      $loading.text('评论加载失败')
    })
  return $cl
}

function createBlogData(blog) {
  var $bd = $('.proto > .blog').clone();

  $bd.find('.avatar').attr(userLinkAttrs(blog.authorId))
    .find('img').attr('src', blog.avatar);
  $bd.find('.author-name').attr(userLinkAttrs(blog.authorId)).text(blog.authorName);
  $bd.find('.title').text(blog.title);
  $bd.find('.content').html(blog.content);
  $bd.find('.time').text(showTime(blog.time)).attr('href', '/blog/'+blog.id);

  var $tags = $bd.find('.tags');
  var tags = blog.tags;
  if (tags && tags.length > 0) {
    $tags.html('');
    $.each(blog.tags, function(idx, tag){
      createTagLabel(tag).appendTo($tags);
    });
  }
  else {
    $tags.remove();
  }

  return $bd;
}

function setupForwardDialog() {
  var $dia = $(renderTmpl("tmpl-modal", {modalId: 'forward-dialog'})).appendTo($('body'))
  $dia.find('.modal-title').text('转发')
  $dia.on('show.bs.modal', function(){
    var $this = $(this)
    var tweetId = $this.data('tweetId')
    if (!tweetId) {throw new Error}
    var tweet = tweetCache.get(tweetId)
    var $nodes = $(renderTmpl('tmpl-forward-dialog', {t: tweet, mfs: tweet.midForwards}))
    nodesCopy('.modal-title', $nodes, $this)
    nodesCopy('.modal-body', $nodes, $this)
    nodesCopy('.modal-footer', $nodes, $this)
  })
}

function replaceMention(content) {
    if (!content) return content
    var indexOfAt = content.indexOf('@');
    var indexOfSpace = content.indexOf(' ', indexOfAt);
    
    if (indexOfAt >= 0 && indexOfSpace > 0) {
        var indexOfInnerAt = content.lastIndexOf('@', indexOfSpace-1);
        if (indexOfInnerAt > indexOfAt && indexOfInnerAt < indexOfSpace) {
            indexOfAt = indexOfInnerAt;
        }
        var mention = content.slice(indexOfAt+1, indexOfSpace);
        var indexOfSharp = mention.indexOf('#');
        if (indexOfSharp > 0) {
            var name = mention.slice(0, indexOfSharp);
            var id = mention.slice(indexOfSharp+1, mention.length);
            return content.slice(0, indexOfAt)
                + $('<a>').text('@'+name).attr(userLinkAttrs(id))[0].outerHTML
                + replaceMention(content.slice(indexOfSpace, content.length));
        }
        else {
            return content.slice(0, indexOfSpace)
                + replaceMention(content.slice(indexOfSpace, content.length));
        }
    }
    return content;
}

function reduceMention(text) {
  if (!text) return text
  var indexOfAt = text.indexOf('@')
  var indexOfSpace = text.indexOf(' ', indexOfAt)
  var indexOfSharp = text.indexOf('#', indexOfAt)

  if (indexOfAt >= 0 && indexOfSpace > 0 && indexOfSharp > 0 && indexOfSharp < indexOfSpace) {
    var indexOfInnerAt = text.lastIndexOf('@', indexOfSharp-1);
    if (indexOfInnerAt > indexOfAt && indexOfInnerAt < indexOfSharp) {
      indexOfAt = indexOfInnerAt;
    }
    return text.slice(0, indexOfSharp) + reduceMention(text.slice(indexOfSpace, text.length))
  }
  return text
}

function stream_setupListeners() {
  $(document).delegate('.tweet-ops .forward', 'click', function () {
    var tweetId = $(this).parents('.tweet').warnEmpty().attr('tweet-id')
    $('#forward-dialog').data('tweetId', tweetId).modal('show')
  })

  $(document).delegate('#forward-dialog .btn-primary', 'click', function() {
    var $dialog = $('#forward-dialog')
    $.post('/post/forward', {
      content: $dialog.find('.input').val(),
      originId: $dialog.data('tweetId'),
      removedIds: $dialog.find('.mf-removed').map(function() {
        return $(this).attr('mf-id')
      }).get()
    }).done(funcLookNewer('/read/istream'))
    $dialog.modal('hide')
  })

  $(document).delegate('.tweet-content .view-img', 'click', function(){
    $(this).toggleClass('view-large')
  })
}