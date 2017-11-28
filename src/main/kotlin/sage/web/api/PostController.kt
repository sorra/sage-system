package sage.web.api

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import sage.domain.commons.RichElement
import sage.web.auth.Auth
import sage.web.context.BaseController

@RestController
@RequestMapping("/post", method = arrayOf(RequestMethod.POST))
class PostController : BaseController() {

  private val logger = LoggerFactory.getLogger(javaClass)

  @RequestMapping("/tweet")
  fun tweet(
      @RequestParam content: String,
      @RequestParam("pictureRef[]", defaultValue = "") pictureRefs: Collection<String>): Boolean {
    val uid = Auth.checkUid()
    val tagIds = tagIds()
    logger.info("Got picture: " + pictureRefs)
    val richElements = pictureRefs.map { RichElement("picture", it) }
    val tweet = tweetPostService.post(uid, content, richElements, tagIds)
    logger.info("post tweet {} success", tweet.id)
    return true
  }

  @RequestMapping("/forward")
  fun forward(
      @RequestParam content: String,
      @RequestParam originId: Long,
      @RequestParam("removedIds[]", defaultValue = "") removedIds: Collection<Long>): Boolean {
    val uid = Auth.checkUid()
    val tweet = tweetPostService.forward(uid, content, originId, removedIds)
    logger.info("forward tweet {} success", tweet.id)
    return true
  }

  @RequestMapping("/comment")
  fun comment(
      @RequestParam content: String,
      @RequestParam sourceId: Long,
      @RequestParam(required = false) replyUserId: Long?,
      @RequestParam(required = false) forward: Boolean?): Boolean {
    val uid = Auth.checkUid()
    //TODO save reply info in the comment
    tweetPostService.comment(uid, content, sourceId, replyUserId)
    if (forward ?: false) {
      forward(content, sourceId, emptyList())
    }
    return true
  }
}
