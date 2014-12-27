'use strict';

template.helper('replaceMention', replaceMention)
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
    var $stream = $('.stream');
    var $slist = $('.slist').empty().warnEmpty();
    $('<a class="newfeed btn">').text('看看新的').prependTo($stream)
        .click(function() {
            var largest = null;
            $('.slist .tweet').each(function(){
                var id = $(this).attr('tweet-id');
                if (id != undefined && id != null && (id > largest || largest == null)) {
                    largest = id;
                }
            });
            console.log("largest "+largest);
            getStreamAfter(url, largest);
        });

    $.each(stream.items, function(idx, item){
        if (item.type == 'TweetCard') {
            createTweetCard(item).appendTo($slist);
        }
        else if (item.type == 'CombineGroup') {
            createCombineGroup(item).appendTo($slist);
        }
    });

    $('<a class="oldfeed btn">').text('看看更早的').appendTo($stream)
        .click(function() {
            var smallest = null;
            $('.slist .tweet').each(function(){
                var id = $(this).attr('tweet-id');
                if (id != undefined && id != null && (id < smallest || smallest == null)) {
                    smallest = id;
                }
            });
            console.log("smallest "+smallest);
            getStreamBefore(url, smallest);
        });
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
  var $tc = $(renderTmpl('tmpl-tweet', tweet))

  $tc.find('a[uid]').mouseenter(launchUcOpener).mouseleave(launchUcCloser)
  $tc.find('.forward').each(forwardDialogEach)
  $tc.find('.comment').click(commentDialog)
  return $tc;
}

function createCombineGroup(group) {
  tweetCache.set(group.origin)
  for(var i in group.forwards) {
    tweetCache.set(group.forwards[i])
  }
  var $cg = $(renderTmpl('tmpl-combine', group))

  $cg.find('a[uid]').mouseenter(launchUcOpener).mouseleave(launchUcCloser)
  $cg.find('.forward').each(forwardDialogEach)
  $cg.find('.comment').click(commentDialog)
  return $cg
}

function forwardDialogEach() {
  var tweetId = $(this).parents('.tweet').warnEmpty().attr('tweet-id')
  if (!tweetId) {console.warn('tweet-id attr is not present on tweet!')}
  var tweet = tweetCache.get(tweetId)
  var $dialog = $(renderTmpl('tmpl-forward-dialog', {t: tweet, mfs: tweet.midForwards}))
  var innerHtml = $dialog.html()

  var submit = function() {
    $.post(webroot+'/post/forward', {
      content: $dialog.find('.input').val(),
      originId: tweetId,
      removedIds: $dialog.find('.mf-removed').map(function () {
        return $(this).attr('mf-id')
      }).get()
    })
    $dialog.modal('hide')
  }

  $dialog.appendTo('#container').modal('hide')
  $(this).click(function () {
    $dialog.html(innerHtml)
    $dialog.find('.btn-primary').click(submit)
    $dialog.modal('show')
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
     $.post(webroot+'/post/comment', {
       content: $input.val(), sourceId: tweetId
     }).success(function(){
       retach(createCommentList(tweetId, retach))
     })
     $input.val('')
  })
  var $loading = $cl.find('.loading')
  var $list = $cl.find('.comment-list')

  $.get(webroot+'/read/'+tweetId+'/comments')
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
  $bd.find('.time').text(showTime(blog.time)).attr('href', webroot+'/blog/'+blog.id);

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

function initConfirmBox($tweet, $del, id) {
    function doDelete(id){
        if (!id) {
            console.warn('this id is '+id)
            return
        }
        $.post(webroot+'/tweet/'+id+'/delete')
          .done(function(resp){
            if(resp == true) {$tweet.remove()}
            else {console.error("Tweet "+id+" delete failed.")}
          })
          .fail(function(resp){console.error("Tweet "+id+" delete failed. Error: "+resp)})
    }
    commonConfirmPopover($del, function(){doDelete(id)}, '确认要删除吗？', 'left')
}

function addDelBtnIfNeeded($tweet, selfId){
	if (selfId === $tweet.attr('author-id')) {
        var $del = $('<a href="javascript:;">').text('删除')
        	.css({marginLeft: '0.5em', marginRight: '0.5em'});
        initConfirmBox($tweet, $del, $tweet.attr('tweet-id'));
        console.log(selfId);
        $tweet.find('.forward:not(.origin .forward)').warnEmpty().before($del);
	}
}
function addDeleteButtons($tweetList){
    $tweetList.warnEmpty().each(function(){addDelBtnIfNeeded($(this), window.userSelf.id);});
}


function replaceMention(content) {
    var indexOfAt = content.indexOf('@');
    var indexOfSpace = content.indexOf(' ', indexOfAt);
    var indexOfInnerAt = content.lastIndexOf('@', indexOfSpace-1);
    
    if (indexOfAt >= 0 && indexOfSpace >=0) {
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