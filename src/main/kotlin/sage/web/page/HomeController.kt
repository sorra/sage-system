package sage.web.page

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import sage.domain.cache.GlobalCaches
import sage.domain.commons.Edge
import sage.entity.Tweet
import sage.transfer.BlogPreview
import sage.transfer.Stream
import sage.web.auth.Auth
import sage.web.context.BaseController

@Controller
@RequestMapping
class HomeController : BaseController() {

  @RequestMapping("/")
  fun index(): ModelAndView {
    return landing()
  }

  @RequestMapping("/home")
  fun home(): ModelAndView {
    val cuid = Auth.checkUid()
    val friends = relationService.friends(cuid)
    return ModelAndView("home").addObject("friends", friends)
  }

  @RequestMapping("/landing")
  fun landing(): ModelAndView {
    val blogs = GlobalCaches.blogsCache["/landing#blogs", {
      blogService.hotBlogs()
    }].map(::BlogPreview)

    val stream = GlobalCaches.tweetsCache["/landing#stream", {
      Tweet.recent(30)
    }].let {
      Stream(streamService.higherSort(transferService.toTweetViews(it), Edge.none().apply { limitCount = 30 }))
    }

    val tags = GlobalCaches.tagsCache["hotTags", {
      tagService.hotTags(5)
    }].map { it.toTagLabel() }

    return ModelAndView("landing").addObject("blogs", blogs).addObject("stream", stream).addObject("tags", tags)
  }

  @RequestMapping("/login")
  fun login(): String = "login"

  @RequestMapping("/logout")
  fun logout(): String = "forward:/auth/logout"

  @RequestMapping("/register")
  fun register(): String = "register"

  @RequestMapping("/rss")
  fun rss(): ModelAndView {
    val blogs = blogService.homeRSS()
    response.contentType = "text/xml"
    return ModelAndView("rss").addObject("blogs", blogs)
  }
}
