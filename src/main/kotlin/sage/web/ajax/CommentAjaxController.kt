package sage.web.ajax

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import sage.entity.Blog
import sage.entity.Comment
import sage.transfer.CommentView
import sage.web.auth.Auth
import sage.web.context.BaseController

@RestController
@RequestMapping("/comments")
open class CommentAjaxController : BaseController() {

  @RequestMapping("/new")
  fun create(@RequestParam content: String, @RequestParam sourceType: Short, @RequestParam sourceId: Long,
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
  fun comments(@RequestParam sourceType: Short, @RequestParam sourceId: Long): Map<String, Any> {
    return mapOf("count" to Comment.count(sourceType, sourceId),
        "list" to Comment.list(sourceType, sourceId).map(::CommentView))
  }
}