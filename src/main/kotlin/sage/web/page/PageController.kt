package sage.web.page

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import sage.service.UserService
import sage.web.auth.Auth

@Controller
open class PageController @Autowired constructor(
    private val userService: UserService
) {
  @RequestMapping("/people")
  open fun people(): ModelAndView {
    val uid = Auth.uid()
    val recomms = if (uid != null) userService.recommendByTag(uid) else emptyList()
    val people = userService.people(uid)
    return ModelAndView("people").addObject("recomms", recomms).addObject("people", people)
  }

  @RequestMapping("/test")
  open fun test(): String {
    return "test"
  }
}
