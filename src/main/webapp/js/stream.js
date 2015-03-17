'use strict';

template.helper('replaceMention', replaceMention)
template.helper('reduceMention', reduceMention)
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

$(document).ready(function() {
  $(document).on('click', '.forward-dialog *[mf-id]', function() {
    $(this).addClass('mf-removed')
  })
})

function getStream(url) {
    return $.get(url, {})
        .done(function(resp){
            if (resp == null) alert('stream is null');
            else createStream(resp, url);
        })
        .fail(function(resp){
            window.alert('stream Oops! ' + resp);
        });
}

function getStreamAfter(url, afterId) {
    if (afterId == undefined) throw new Error;
    return $.get(url, {after: afterId})
        .done(function(resp){
            if (resp == null) alert('stream is null');
            else createStreamAfter(resp, url);
        })
        .fail(function(resp){
            window.alert('stream Oops! ' + resp);
        });
}

function getStreamBefore(url, beforeId) {
    if (beforeId == undefined) throw new Error;
    return $.get(url, {before: beforeId})
        .done(function(resp){
            if (resp == null) alert('stream is null');
            else createStreamBefore(resp, url);
        })
        .fail(function(resp){
            window.alert('stream Oops! ' + resp);
        });
}

function createStream(stream, url) {
  var $stream = $('.stream')
  var $slist = $('.slist').empty().warnEmpty()
  $.each(stream.items, function(idx, item){
    if (item.type == 'TweetCard') {
      createTweetCard(item).appendTo($slist)
    }
    else if (item.type == 'CombineGroup') {
      createCombineGroup(item).appendTo($slist)
    }
  })

  $('<a class="newfeed btn">').text('看看新的').prependTo($stream)
    .click(function() {
      var largest = null
      $('.slist .tweet').each(function(){
        var id = parseInt($(this).attr('tweet-id'))
        if (id && (id > largest || largest == null)) {
          largest = id
        }
      })
      console.log("largest "+largest)
      if(largest == null) {
        getStream(url)
      } else {
        getStreamAfter(url, largest)
      }
    })

  $('<a class="oldfeed btn">').text('看看更早的').appendTo($stream)
    .click(function() {
      var smallest = null
      $('.slist .tweet').each(function(){
        var id = parseInt($(this).attr('tweet-id'))
        if (id && (id < smallest || smallest == null)) {
          smallest = id
        }
      })
      console.log("smallest "+smallest)
      if (smallest == null) {
        getStream(url)
      } else {
        getStreamBefore(url, smallest)
      }
    })
}

function createStreamAfter(stream) {
  if (stream.items.length == 0) {
    tipover($('.stream .newfeed').warnEmpty(), '还没有新的')
  }
  var $slist = $('.slist');
  $.each(stream.items.reverse(), function(idx, item){
    createStreamItem(item).prependTo($slist)
  });
}

function createStreamBefore(stream) {
  if (stream.items.length ==0) {
    tipover($('.stream .oldfeed').warnEmpty(), '没有更早的了')
  }
  var $slist = $('.slist')
  $.each(stream.items, function(idx, item){
    createStreamItem(item).appendTo($slist)
  });
}

function createStreamItem(item) {
  if (item.type == 'TweetCard') {return createTweetCard(item)}
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
  $elem.find('.tweet-ops .forward').each(forwardDialogEach)
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

function forwardDialogEach() {
  var tweetId = $(this).parents('.tweet').warnEmpty().attr('tweet-id')
  if (!tweetId) {console.warn('tweet-id attr is not present on tweet!')}

  var submit = function() {
    var $dialog = $('#forward-dialog')
    $.post('/post/forward', {
      content: $dialog.find('.input').val(),
      originId: tweetId,
      removedIds: $dialog.find('.mf-removed').map(function () {
        return $(this).attr('mf-id')
      }).get()
    })
    $dialog.modal('hide')
  }

  $(this).click(function () {
    $('#forward-dialog').data('tweetId', tweetId).modal('show').find('.btn-primary').click(submit)
  })
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