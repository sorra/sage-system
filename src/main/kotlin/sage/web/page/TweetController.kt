package sage.web.page

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import sage.entity.Tweet
import sage.entity.getRecordsCount
import sage.service.TransferService
import sage.service.TweetReadService
import sage.util.PaginationLogic
import sage.web.context.RenderUtil

@Controller
@RequestMapping("/tweets")
open class TweetController @Autowired constructor(
    private val tweetReadService: TweetReadService,
    private val transferService: TransferService
) {
  @RequestMapping("/{id}")
  open fun tweetPage(@PathVariable id: Long): ModelAndView {
    val tweet = tweetReadService.getTweetView(id)
        ?: return ModelAndView("forward:/not-found")
    return ModelAndView("tweet-page").addObject("tweet", tweet)
  }

  // TODO no page yet
  @RequestMapping
  open fun tweets(@RequestParam(defaultValue = "1") page: Int): ModelAndView {
    val size = 20
    val recordsCount: Long = getRecordsCount(Tweet)
    val pagesCount: Int = PaginationLogic.pagesCount(size, recordsCount)
    val tweets = Tweet.orderBy("id desc").findPagedList(page-1, size).list
        .map { transferService.toTweetView(it) }
    return ModelAndView("tweets").addObject("tweets", tweets)
        .addObject("paginationLinks", RenderUtil.paginationLinks("/tweets", pagesCount, page))
  }
}