package sage.web.page

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import sage.entity.Blog
import sage.entity.User
import sage.transfer.BlogPreview
import sage.util.Strings
import sage.web.auth.Auth
import sage.web.context.BaseController
import sage.web.context.FrontMap

@Controller
@RequestMapping("/users")
open class UserController : BaseController() {

  @RequestMapping("/self")
  open fun userPage() = "redirect:/users/${Auth.checkUid()}"

  @RequestMapping("/{id}")
  open fun userPage(@PathVariable id: Long): ModelAndView {
    val uid = Auth.uid()
    val thisUser = userService.getUserCard(uid, id)
    val blogs = Blog.byAuthor(id).map(::BlogPreview)
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
  open fun people(): ModelAndView {
    val uid = Auth.uid()
    val recomms = if (uid != null) userService.recommendByTag(uid) else emptyList()
    val people = userService.people(uid)
    return ModelAndView("people").addObject("recomms", recomms).addObject("people", people)
  }

  @RequestMapping("/{id}/rss")
  open fun rss(@PathVariable id: Long): ModelAndView {
    val blogs = Blog.byAuthor(id)
    response.contentType = "text/xml"
    blogs.forEach {
      it.content = Strings.escapeXmlInvalidChar(it.content)
    }
    return ModelAndView("rss").addObject("blogs", blogs).addObject("name", User.get(id).name)
  }
}
