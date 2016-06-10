package sage.web.api

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import sage.domain.commons.Edge
import sage.service.StreamService
import sage.service.TransferService
import sage.service.TweetReadService
import sage.transfer.CommentView
import sage.transfer.Stream
import sage.transfer.TweetView
import sage.web.auth.Auth

@RestController
@RequestMapping("/read")
open class ReadController {
  @Autowired
  private val tweetReadService: TweetReadService? = null
  @Autowired
  private val transfers: TransferService? = null

  @RequestMapping("/connect/{blogId}")
  open fun connect(@PathVariable blogId: Long?): Stream {
    val tcs = tweetReadService!!.connectTweets(blogId!!)
    return Stream(tcs)
  }

  @RequestMapping("/{tweetId}/forwards")
  open fun forwards(@PathVariable tweetId: Long?): Collection<TweetView> {
    return transfers!!.toTweetViews(tweetReadService!!.getForwards(tweetId!!), false, false)
  }
}
