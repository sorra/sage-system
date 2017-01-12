package sage.transfer

import sage.annotation.KotlinNoArg
import java.sql.Timestamp

import sage.entity.TopicReply

@KotlinNoArg
class TopicReplyView {
  var id: Long = 0
  var content: String = ""
  var topicPostId: Long = 0
  var author: UserLabel? = null
  var whenCreated: Timestamp? = null
  var floorNumber: Int = 0

  var toUser: UserLabel? = null
  var toReplyId: Long? = null

  constructor(reply: TopicReply, toUserLabel: UserLabel?) {
    id = reply.id
    content = reply.content
    topicPostId = reply.topicPostId
    author = UserLabel(reply.author)
    whenCreated = reply.whenCreated
    floorNumber = reply.floorNumber

    toUser = toUserLabel
    toReplyId = reply.toReplyId
  }
}
