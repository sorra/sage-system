package sage.web.page

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import sage.domain.cache.GlobalCaches
import sage.entity.Blog
import sage.entity.Tweet
import sage.service.*
import sage.util.Strings
import sage.web.auth.Auth
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping
open class HomeController
@Autowired constructor(
    private val relationService: RelationService,
    private val topicService: TopicService,
    private val blogService: BlogService,
    private val transferService: TransferService) {

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
    val topics = GlobalCaches.topicsCache["/landing#topics", {
      topicService.hotTopics().apply { if(uid == null) take(10) }
    }]
    val blogs = GlobalCaches.blogsCache["/landing#blogs", {
      blogService.hotBlogs().apply { if (uid == null) take(10) }
    }]
    val tweets = GlobalCaches.tweetsCache["/landing#tweets", {
      Tweet.recent(10).map { transferService.toTweetView(it) }
    }]
    return ModelAndView("landing").addObject("topics", topics).addObject("blogs", blogs).addObject("tweets", tweets)
  }

  @RequestMapping("/login")
  open fun login(): String = "login"

  @RequestMapping("/logout")
  open fun logout(): String = "forward:/auth/logout"

  @RequestMapping("/register")
  open fun register(): String = "register"

  @RequestMapping("/rss")
  open fun rss(response: HttpServletResponse): ModelAndView {
    val blogs = Blog.orderBy("id desc").findList()
    blogs.forEach { it.content = Strings.omit(it.content, 500) }
    response.contentType = "text/xml"
    return ModelAndView("rss").addObject("blogs", blogs)
  }
}
