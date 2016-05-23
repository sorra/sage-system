function login_setup() {
  $('form').submit(function(event) {
    try {
      var valid = login_validate()
    } catch (e) {
      event.preventDefault()
      alert('登录未能成功: ' + e)
      throw e
    }
    if (!valid ) {
      event.preventDefault()
    }
  })
}

function login_validate() {
  var count = 0
  function alertNode(text) {
    count++
    return $('<span class="validation-alert"></span>').text(text)
  }
  $('.validation-alert').remove()

  var email = $('#email').val()
  if (email.length == 0) {
    alertNode('请输入邮箱').insertAfter($('#email'))
  } else {
    var idxOfAt = email.indexOf('@')
    if (idxOfAt <= 0 || email.indexOf('.', idxOfAt) <= 0) {
      alertNode('请输入有效的邮箱地址').insertAfter($('#email'))
    }
  }

  var password = $('#password').val()
  if(password.length == 0) {
    alertNode('请输入密码').insertAfter($('#password'))
  }

  return count == 0
}