package sage.web.page

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import sage.entity.Blog
import sage.service.UserService
import sage.transfer.BlogPreview
import sage.web.auth.Auth
import sage.web.context.FrontMap

@Controller
@RequestMapping("/users")
open class UserController @Autowired constructor(
    private val userService: UserService
) {

  @RequestMapping("/self")
  open fun userPage() = "redirect:/users/${Auth.checkUid()}"

  @RequestMapping("/{id}")
  open fun userPage(@PathVariable id: Long): ModelAndView {
    val uid = Auth.uid()
    val thisUser = userService.getUserCard(uid, id)
    val blogs = Blog.byAuthor(id).sortedByDescending { it.whenCreated }.map(::BlogPreview)
    return ModelAndView("user-page").addObject("thisUser", thisUser)
        .addObject("blogs", blogs)
        .include(FrontMap().attr("id", id).attr("isSelfPage", uid == id))
  }

  @RequestMapping("/{id}/card")
  open fun userCard(@PathVariable id: Long): ModelAndView {
    val uid = Auth.checkUid()
    val theUser = userService.getUserCard(uid, id)
    return ModelAndView("user-card").addObject("user", theUser)
  }

  @RequestMapping
  open fun all() = "redirect:/people"
}
