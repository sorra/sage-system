package sage.web.page.admin

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import sage.domain.commons.DomainException
import sage.domain.concept.Authority
import sage.entity.User
import sage.service.UserService
import sage.web.auth.Auth
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/admin")
open class AdminController @Autowired constructor(private val userService: UserService) {

  @RequestMapping("/user-info", method = arrayOf(RequestMethod.GET))
  open fun userInfo(response: HttpServletResponse): String {
    if (User.get(Auth.checkUid()).authority != Authority.SITE_ADMIN) {
      response.sendError(404)
    }
    return "admin-page-user-info"
  }

  @RequestMapping("/user-info", method = arrayOf(RequestMethod.POST))
  @ResponseBody
  open fun changeUserInfo(@RequestParam userId: Long, request: HttpServletRequest, response: HttpServletResponse): String {
    if (User.get(Auth.checkUid()).authority != Authority.SITE_ADMIN) {
      response.sendError(404)
    }
    val email = request.getParameter("email") as String?
    val password = request.getParameter("password") as String?
    val name = request.getParameter("name") as String?
    val intro = request.getParameter("intro") as String?

    if (email != null) {
      User.get(userId).run { this.email = email; update() }
    }
    if (password != null) {
      userService.resetPassword(userId, password)
    }
    if (name != null || intro != null) {
      userService.changeInfo(userId, name, intro, null)
    }
    return "/users/$userId"
  }
}