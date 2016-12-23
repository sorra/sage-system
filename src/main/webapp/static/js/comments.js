function comments_render(el, sourceType, sourceId) {
  $(el).html($('#tmpl-comment-list').html())
  return new Vue({
    el: el,
    data: {
      sourceType: sourceType,
      sourceId: sourceId,
      count: 0,
      comments: [],
      visible: true,
      editorContent: '',
      commentBeingReplied: null
    },

    ready: function () {
      this.$emit('fetch')
    },

    events: {
      fetch: function () {
        var self = this
        $.get('/comments', {sourceType: this.sourceType, sourceId: this.sourceId})
          .done(function (object) {
            self.count = object.count
            self.comments = object.list
            self.alert('alert-info', '评论加载成功')
          })
          .fail(function (resp) {
            self.alert('alert-danger', errorMsg(resp))
          })
      }
    },

    methods: {
      postComment: function (alsoForward) {
        var self = this
        if (!self.editorContent || self.editorContent.trim() == '') {
          self.alert('alert-danger', '请输入内容')
          return
        }
        self.showAlert('alert-warning', '正在发送...')
        $.post('/comments/new', {
          content: this.editorContent,
          sourceType: this.sourceType,
          sourceId: this.sourceId,
          forward: alsoForward
        }).done(function () {
          self.editorContent = ''
          self.alert('alert-success', '发送成功')
        }).fail(function (resp) {
          self.alert('alert-danger', '发送失败: ' + errorMsg(resp))
        })
      },

      postReply: function (c, alsoForward) {
        var self = this
        if (!c.replyEditorContent || c.replyEditorContent.trim() == '') {
          self.alert('alert-danger', '请输入内容', true)
          return
        }
        self.showAlert('alert-warning', '正在发送...', true)
        $.post('/comments/new', {
          content: c.replyEditorContent,
          sourceType: this.sourceType,
          sourceId: this.sourceId,
          replyUserId: c.authorId,
          forward: alsoForward
        }).done(function () {
          c.replyEditorContent = ''
          self.commentBeingReplied = null
          self.alert('alert-success', '发送成功', true)
        }).fail(function (resp) {
          self.alert('alert-danger', '发送失败: ' + errorMsg(resp), true)
        })
      },

      toggleWriteReply: function (c) {
        if (this.commentBeingReplied != c) {
          this.commentBeingReplied = c
        } else {
          this.commentBeingReplied = null
        }
      },

      showAlert: function (cls, msg, isReply) {
        var queryPrefix = isReply ? '.comment[data-id=' + this.commentBeingReplied.id + '] ' : '.main-comment-editor '
        return $(this.$el).find(queryPrefix + '.action-alert').attr('class', 'action-alert ' + cls).text(msg).stop().css('opacity', '1').show()
      },

      alert: function (cls, msg, isReply) {
        return this.showAlert(cls, msg, isReply).fadeOut(2000)
      }
    }
  })
}

function toggleTweetComments(){
  var $tweet = $(this).parents('.tweet')
  var tweetId = $tweet.attr('tweet-id')
  var $commentsContainer = $tweet.find('.comments-container')
  var vm = $commentsContainer.data('vm')
  if (vm) {
    if (vm.$data.visible) {
      vm.$data.visible = false
    } else {
      vm.$data.visible = true
      vm.$emit('fetch')
    }
  } else {
    vm = comments_render($tweet.find('.comments-container')[0], 2, tweetId)
    $commentsContainer.data('vm', vm)
  }
}
