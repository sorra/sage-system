package sage.web.ajax

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import sage.entity.Blog
import sage.entity.Comment
import sage.service.TweetPostService
import sage.transfer.CommentView
import sage.web.auth.Auth

@RestController
@RequestMapping("/comments")
open class CommentController @Autowired constructor(
    private val tweetPostService: TweetPostService) {

  @RequestMapping("/new")
  open fun create(@RequestParam content: String, @RequestParam sourceType: Short, @RequestParam sourceId: Long,
                  @RequestParam(required = false) replyUserId: Long?,
                  @RequestParam(required = false) forward: Boolean?) {
    val uid = Auth.checkUid()
    if (sourceType == Comment.TWEET) {
      tweetPostService.comment(uid, content, sourceId, replyUserId)
      if (forward ?: false) {
        tweetPostService.forward(uid, content, sourceId, emptyList())
      }
    } else if (sourceType == Comment.BLOG) {
      val tweetId = Blog.get(sourceId).tweetId
      tweetPostService.comment(uid, content, tweetId, replyUserId)
    }
  }

  @RequestMapping
  open fun comments(@RequestParam sourceType: Short, @RequestParam sourceId: Long): Map<String, Any> {
    return mapOf("count" to Comment.count(sourceType, sourceId),
        "list" to Comment.list(sourceType, sourceId).map(::CommentView))
  }
}