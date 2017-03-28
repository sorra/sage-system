package sage.web.page

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import sage.entity.Tweet
import sage.entity.getRecordsCount
import sage.transfer.TweetGroup
import sage.util.PaginationLogic
import sage.web.context.BaseController
import sage.web.context.RenderUtil

@Controller
@RequestMapping("/tweets")
class TweetController : BaseController() {
  @RequestMapping("/{id}")
  fun tweetPage(@PathVariable id: Long): ModelAndView {
    val tweetGroup = tweetReadService.getTweetView(id)?.let {
      if (it.origin != null) TweetGroup.createByForward(it)
      else TweetGroup.createByOrigin(it)
    } ?: return ModelAndView("forward:/errors/not-found")
    return ModelAndView("tweet-page").addObject("group", tweetGroup)
  }

  // TODO no page yet
  @RequestMapping("/{id}/forwards")
  fun forwards(@PathVariable id: Long): ModelAndView {
    val forwards = transferService.toTweetViews(tweetReadService.getForwards(id), false, false)
    return ModelAndView("tweet-forwards").addObject("forwards", forwards)
  }

  // TODO no page yet
  @RequestMapping
  fun tweets(): ModelAndView {
    val pageIndex = pageIndex()
    val pageSize = pageSize()

    val recordsCount: Long = getRecordsCount(Tweet)
    val pagesCount: Int = PaginationLogic.pagesCount(pageSize, recordsCount)
    val tweets = Tweet.orderBy("id desc").findPagedList(pageIndex-1, pageSize).list
        .map { transferService.toTweetView(it) }

    return ModelAndView("tweets")
        .addObject("tweets", tweets)
        .addObject("paginationLinks", RenderUtil.paginationLinks("/tweets", pagesCount, pageSize))
  }
}