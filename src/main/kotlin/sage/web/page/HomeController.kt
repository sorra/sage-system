package sage.web.page

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import sage.service.BlogService
import sage.service.RelationService
import sage.service.TopicService
import sage.transfer.BlogPreview
import sage.web.auth.Auth
import sage.web.context.FrontMap

@Controller
@RequestMapping
open class HomeController
@Autowired constructor(
    private val relationService: RelationService,
    private val topicService: TopicService,
    private val blogService: BlogService) {

  @RequestMapping("/")
  open fun index(model: ModelMap): ModelAndView {
    return landing()
  }

  @RequestMapping("/home")
  open fun home(): ModelAndView {
    val cuid = Auth.checkUid()
    val friends = relationService.friends(cuid)
    return ModelAndView("home").addObject("friends", friends)
  }

  @RequestMapping("/landing")
  open fun landing(): ModelAndView {
    val uid = Auth.uid()
    val hotTopics = topicService.hotTopics().apply { if(uid == null) take(10) }
    val blogs = blogService.pickedBlogs().map { BlogPreview(it) }.apply { if(uid == null) take(10) }
    return ModelAndView("landing").addObject("hotTopics", hotTopics).addObject("blogs", blogs)
  }

  @RequestMapping("/login")
  open fun login(): String = "login"

  @RequestMapping("/logout")
  open fun logout(): String = "forward:/auth/logout"

  @RequestMapping("/register")
  open fun register(): String = "register"
}
