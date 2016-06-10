package sage.transfer

import sage.entity.Comment
import java.util.*

class CommentView {
  var id: Long = 0
  var content: String = ""
  var authorId: Long = 0
  var authorName: String = ""
  var avatar: String = ""
  var whenCreated: Date? = null
  var replyUserId: Long? = null

  internal constructor() {
  }

  constructor(comment: Comment) {
    id = comment.id
    content = comment.content
    authorId = comment.author.id
    authorName = comment.author.name
    avatar = comment.author.avatar
    whenCreated = comment.whenCreated
    replyUserId = comment.replyUserId
  }
}
