package sage.web.page

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import sage.entity.Tweet
import sage.transfer.TweetGroup
import sage.transfer.TweetView
import sage.web.auth.Auth
import sage.web.context.BaseController

@Controller
class SearchPageController : BaseController() {
  @RequestMapping("/search")
  fun search(@RequestParam q: String, model: ModelMap): String {
    if (q.isEmpty()) {
      return "forward:/"
    }

    val uid = Auth.checkUid()
    logger.info("/search uid=$uid, query=$q")

    val blogs = blogService.search(q)
    val tweets = tweetReadService.search(q)

    model
        .addAttribute("blogs", blogs)
        .addAttribute("tweets", tweets)

    return "search-result"
  }

  companion object {
    private val logger = LoggerFactory.getLogger(SearchPageController::class.java)
  }
}
