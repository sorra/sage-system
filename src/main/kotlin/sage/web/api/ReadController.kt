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
import sage.transfer.CommentCard
import sage.transfer.Stream
import sage.transfer.TweetView
import sage.web.auth.Auth

@RestController
@RequestMapping("/read")
open class ReadController {
  @Autowired
  private val streamService: StreamService? = null
  @Autowired
  private val tweetReadService: TweetReadService? = null
  @Autowired
  private val transfers: TransferService? = null

  @RequestMapping("/istream")
  open fun istream(
      @RequestParam(required = false) before: Long?,
      @RequestParam(required = false) after: Long?): Stream {
    val cuid = Auth.checkUid()
    return streamService!!.istream(cuid, getEdge(before, after))
  }

  @RequestMapping("istream-by-tag")
  open fun istreamByTag(
      @RequestParam tagId: Long?,
      @RequestParam(required = false) before: Long?,
      @RequestParam(required = false) after: Long?): Stream {
    val cuid = Auth.checkUid()
    return streamService!!.istreamByTag(cuid, tagId!!, getEdge(before, after))
  }

  @RequestMapping("/connect/{blogId}")
  open fun connect(@PathVariable blogId: Long?): Stream {
    val tcs = tweetReadService!!.connectTweets(blogId!!)
    return Stream(tcs)
  }

  @RequestMapping("/{tweetId}/forwards")
  open fun forwards(@PathVariable tweetId: Long?): Collection<TweetView> {
    return transfers!!.toTweetViews(tweetReadService!!.getForwards(tweetId!!), false, false)
  }

  @RequestMapping("/{tweetId}/comments")
  open fun comments(@PathVariable tweetId: Long?): Collection<CommentCard> {
    return CommentCard.listOf(tweetReadService!!.getComments(tweetId!!))
  }

  @RequestMapping("/tag/{id}")
  open fun tagStream(@PathVariable id: Long?,
                     @RequestParam(required = false) before: Long?,
                     @RequestParam(required = false) after: Long?): Stream {
    return streamService!!.tagStream(id!!, getEdge(before, after))
  }

  @RequestMapping("/u/{id}")
  open fun personalStream(@PathVariable id: Long?,
                          @RequestParam(required = false) before: Long?,
                          @RequestParam(required = false) after: Long?): Stream {
    return streamService!!.personalStream(id!!, getEdge(before, after))
  }

  private fun getEdge(beforeId: Long?, afterId: Long?): Edge {
    if (beforeId == null && afterId == null) {
      return Edge.none()
    } else if (beforeId != null && afterId != null) {
      throw UnsupportedOperationException()
    } else if (beforeId != null) {
      return Edge.before(beforeId)
    } else if (afterId != null) {
      return Edge.after(afterId)
    }
    throw UnsupportedOperationException()
  }

  companion object {
    private val logger = LoggerFactory.getLogger(ReadController::class.java)
  }
}
