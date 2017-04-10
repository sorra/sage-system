function home_setup() {
  $('#nav-home').addClass('active');
  $(document).on('mouseenter', 'a[uid]', launchUcOpener).on('mouseleave', 'a[uid]', launchUcCloser)

  var $postBox = $($('.post-box')[0]).attr('id', 'the-post-box')
  $postBox.find('.pic-upload input[type=file]').ajaxfileupload({
    action: '/upload/pic',
    onComplete: function (resp) {
      if (!resp.location) {
        return
      }
      var $postBox = $('#the-post-box')
      $postBox.find('input[name=pictureRef]').val(resp.location)
      $postBox.find('.pic-preview').show().find('img').attr('src', resp.location)
    },
    onStart: function () {
      console.info('Upload tweet picture')
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

  getStream('/stream/i')

  setInterval(funcLookNewer(true), 10000)
}

function postTweetDone() {
  $('.post-box .input').val('')
  $('.post-box .pic-preview img').attr('src', '')
  hideTagTreeInput($('.tag-plus'))
  popAlert('发表成功', 'success')
  funcLookNewer(true)()
}

function postTweetFail() {
  popAlert('发表失败')
}