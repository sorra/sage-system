package sage.web.page

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import sage.domain.commons.BadArgumentException
import sage.domain.commons.DomainException
import sage.entity.User
import sage.web.auth.Auth
import sage.web.context.BaseController
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/auth")
class AuthController : BaseController() {

  @PostMapping("/login")
  fun login(@RequestParam email: String,
                 @RequestParam password: String,
                 @RequestParam(defaultValue = "false") rememberMe: Boolean): String {
    if (email.isEmpty() || password.isEmpty()) {
      throw DomainException("Empty input!")
    }

    log.info("Trying login email: {}", email)
    Auth.logout()
    val user = userService.login(email, password)
    Auth.login(user.id, rememberMe)
    log.info("User[{}] login successfully.", user)

    val referer = request.getHeader("referer")
    log.debug("Referer: {}", referer)

    val destContext = "?goto="
    val idx = referer.lastIndexOf(destContext)
    var dest: String? =
        if (idx < 0) null
        else referer.substring(idx + destContext.length, referer.length)
    if (dest != null && dest.contains(":")) {
      log.info("Login dest got XSS URL = {}", dest)
      dest = null // Escape cross-site url
    }

    return "redirect:" + (if (dest == null) "/" else Auth.decodeLink(dest))
  }

  @RequestMapping("/logout")
  fun logout(): String {
    Auth.logout()
    log.info("User[{}] logout successfully.", Auth.uid())
    return "redirect:/login"
  }

  @PostMapping("/register")
  fun register(request: HttpServletRequest,
                    @RequestParam email: String,
                    @RequestParam password: String,
                    @RequestParam(required = false) repeatPassword: String?): String {
    log.info("Try to register email: {}", email)

    if (email.length > 50) {
      throw EMAIL_TOO_LONG
    }
    val idxOfAt = email.indexOf('@')
    if (idxOfAt <= 0 || email.indexOf('.', idxOfAt) <= 0) {
      throw EMAIL_WRONG_FORMAT
    }

    if (password.length < 8) {
      throw PASSWORD_TOO_SHORT
    }
    if (password.length > 20) {
      if (password.contains(",")) {
        log.error("密码含有逗号, 是不是表单的name=password重复了?")
      }
      throw PASSWORD_TOO_LONG
    }
    if (repeatPassword != null && repeatPassword != password) {
      throw REPEAT_PASSWORD_NOT_MATCH
    }

    userService.register(User(email, password))
    login(email, password, false)
    return "redirect:/user-info?next=/people"
  }

  companion object {
    private val log = LoggerFactory.getLogger(AuthController::class.java)

    private val EMAIL_TOO_LONG = BadArgumentException("Email不能超过50个字符")
    private val EMAIL_WRONG_FORMAT = BadArgumentException("Email格式错误")
    private val PASSWORD_TOO_SHORT = BadArgumentException("密码太短，应为8~20位")
    private val PASSWORD_TOO_LONG = BadArgumentException("密码太长，应为8~20位")
    private val REPEAT_PASSWORD_NOT_MATCH = BadArgumentException("两次输入的密码不一致")
  }
}
