"use strict";
function register_setup() {
  $('form').submit(function(event) {
    try {
      var valid = register_validate()
    } catch (e) {
      event.preventDefault()
      alert('注册未能成功: ' + e)
      throw e
    }
    if (!valid ) {
      event.preventDefault()
    }
  })

}

function register_validate() {
  var count = 0
  function alertNode(text) {
    count++
    return $('<span class="validation-alert"></span>').text(text)
  }
  $('.validation-alert').remove()

  var email = $('#email').val()
  if (email.length == 0) {
    alertNode('请输入内容').insertAfter($('#email'))
  } else {
    var idxOfAt = email.indexOf('@')
    if (idxOfAt <= 0 || email.indexOf('.', idxOfAt) <= 0) {
      alertNode('请输入有效的邮箱地址').insertAfter($('#email'))
    }
  }

  var password = $('#password').val()
  if(password.length == 0) {
    alertNode('请输入内容').insertAfter($('#password'))
  } else {
    if (password.length < 8 || password.length > 20) {
      alertNode('密码应为8~20位').insertAfter($('#password'))
    }
  }

  var repeat = $('#repeat-password').val()
  if (password != repeat) {
    alertNode('两次密码输入不一致').insertAfter($('#repeat-password'))
  }
  
  return count == 0
}