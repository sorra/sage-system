package sage.web.page.admin

import com.avaje.ebean.Ebean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import sage.domain.constraints.Authority
import sage.entity.User
import sage.web.auth.Auth
import sage.web.context.BaseController

@Controller
@RequestMapping("/admin")
class AdminController : BaseController() {

  @GetMapping("/user-info")
  fun userInfo(): String {
    if (User.get(Auth.checkUid()).authority != Authority.SITE_ADMIN) {
      response.sendError(404)
    }
    return "admin-page-user-info"
  }

  @PostMapping("/user-info")
  @ResponseBody
  fun changeUserInfo(@RequestParam userId: Long): String {
    if (User.get(Auth.checkUid()).authority != Authority.SITE_ADMIN) {
      response.sendError(404)
      return ""
    }
    val email = request.getParameter("email")
    val password = request.getParameter("password")
    val name = request.getParameter("name")
    val intro = request.getParameter("intro")

    if (email != null) {
      if (User.byEmail(email) != null) {
        return "Email duplicate: $email"
      }
      val user = User.get(userId)
      user.email = email
      user.update()
    }
    if (password != null) {
      userService.resetPassword(userId, password)
    }
    if (name != null || intro != null) {
      userService.changeInfo(userId, name, intro, null)
    }
    return "/users/$userId"
  }

  @GetMapping("/sql")
  fun sql(): String {
    if (User.get(Auth.checkUid()).authority != Authority.SITE_ADMIN) {
      response.sendError(404)
      return ""
    }
    return "admin-page-sql"
  }

  @PostMapping("/sql")
  fun submitSql(@RequestParam statement: String): ModelAndView {
    val results = if (statement.trim().toLowerCase().startsWith("select")) {
      Ebean.getDefaultServer().createSqlQuery(statement).findList().map { it.toString() }
    } else {
      listOf("Updated: " + Ebean.getDefaultServer().createSqlUpdate(statement).execute())
    }
    return ModelAndView("admin-page-sql").addObject("results", results)
  }
}