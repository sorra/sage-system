package sage.web.page

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import sage.entity.Blog
import sage.entity.User
import sage.transfer.BlogPreview
import sage.web.auth.Auth
import sage.web.context.BaseController

@Controller
@RequestMapping("/users")
class UserController : BaseController() {

  @RequestMapping("/self")
  fun userPage() = "redirect:/users/${Auth.checkUid()}"

  @RequestMapping("/{id}")
  fun userPage(@PathVariable id: Long): ModelAndView {
    val uid = Auth.uid()
    val thisUser = userService.getUserCard(uid, id)
    val blogs = Blog.byAuthor(id).map(::BlogPreview)

    frontMap().attr("id", id).attr("isSelfPage", uid == id)

    return ModelAndView("user-page").addObject("thisUser", thisUser)
        .addObject("blogs", blogs)
  }

  @RequestMapping("/{id}/card")
  fun userCard(@PathVariable id: Long): ModelAndView {
    val uid = Auth.checkUid()
    val theUser = userService.getUserCard(uid, id)
    return ModelAndView("user-card").addObject("user", theUser)
  }

  @RequestMapping
  fun people(): ModelAndView {
    val uid = Auth.uid()
    val recomms = if (uid != null) userService.recommendByTag(uid) else emptyList()
    val people = userService.people(uid)
    return ModelAndView("people").addObject("recomms", recomms).addObject("people", people)
  }

  @RequestMapping("/{id}/rss")
  fun rss(@PathVariable id: Long): ModelAndView {
    val blogs = blogService.authorRSS(id)
    response.contentType = "text/xml"
    return ModelAndView("rss").addObject("blogs", blogs).addObject("name", User.get(id).name)
  }
}
