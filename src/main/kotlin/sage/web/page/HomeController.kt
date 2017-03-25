package sage.web.page

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import sage.domain.cache.GlobalCaches
import sage.domain.commons.Edge
import sage.entity.Blog
import sage.entity.Tweet
import sage.transfer.BlogPreview
import sage.transfer.Stream
import sage.transfer.TagLabel
import sage.util.Strings
import sage.web.auth.Auth
import sage.web.context.BaseController

@Controller
@RequestMapping
open class HomeController : BaseController() {

  @RequestMapping("/")
  open fun index(): ModelAndView {
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
  open fun login(): String = "login"

  @RequestMapping("/logout")
  open fun logout(): String = "forward:/auth/logout"

  @RequestMapping("/register")
  open fun register(): String = "register"

  @RequestMapping("/rss")
  open fun rss(): ModelAndView {
    val blogs = Blog.orderBy("id desc").findList()
    response.contentType = "text/xml"
    return ModelAndView("rss").addObject("blogs", blogs)
  }
}
