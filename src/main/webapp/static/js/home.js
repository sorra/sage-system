'use strict';

function home_setup() {
  $('#nav-home').addClass('active');
  $(document).on('mouseenter', 'a[uid]', launchUcOpener).on('mouseleave', 'a[uid]', launchUcCloser)

  var $postBox = $($('.post-box')[0]).attr('id', 'the-post-box')
  $postBox.find('.file-upload').ajaxfileupload({
    action: '/pic-upload',
    onComplete: function (resp) {
      var $postBox = $('#the-post-box')
      $postBox.find('input[name=pictureRef]').val(resp)
      $postBox.find('.pic-preview').show().find('img').attr('src', resp)
    },
    onStart: function () {
      console.info('~~~')
    }
  })
  $postBox.find('.pic-preview').click(function () {
    $(this).find('img').toggle()
  })


  // prepare tweet-submit-button
  $postBox.find('.btn[type="submit"]')
    .tooltip({
      placement: 'top',
      trigger: 'manual'
    })
    .click(function (event) {
      event.preventDefault();
      var $submit = $(this);
      $submit.prop('disabled', true);

      var selectedTagIds = [];
      $('.tag-sel.btn-success').each(function (idx) {
        var tagId = parseInt($(this).attr('tag-id'));
        selectedTagIds.push(tagId);
        $(this).removeClass('.btn-success');
      });

      var input = $('form.post-tweet .input').val();
      if (input.trim().length == 0) {
        postTweetFail();
        $submit.prop('disabled', false);
        return;
      }

      var data = {
        content: input,
        tagIds: selectedTagIds
      }

      var picturePath = $('#the-post-box').find('input[name=pictureRef]').val()
      if (picturePath) {
        data['pictureRef'] = [picturePath]
      }

      $.post('/post/tweet', data)
        .always(function (resp) {
          $submit.prop('disabled', false)
        })
        .done(function (resp) {
          console.log(resp);
          if (resp == true) postTweetDone()
          else postTweetFail();
        })
        .fail(function (resp) {
          postTweetFail()
        })
    })

  // load istream
  getStream('/stream/i')

  setInterval(funcLookNewer('/stream/i', function (resp) {
    if (resp) {
      tipover($('.stream .newfeed').warnEmpty(), '又出现了' + stream.items.length + '组新信息')
    }
  }), 5000)
}

function postTweetDone() {
  var $submit = $('form.post-tweet .btn[type="submit"]')
  $('form.post-tweet .input').val('')
  hideTagTreeInput($('.tag-plus'))
  tipover($submit, '发表成功')
  funcLookNewer('/stream/i')()
}

function postTweetFail() {
  var $submit = $('form.post-tweet .btn[type="submit"]')
  tipover($submit, '发表失败')
}