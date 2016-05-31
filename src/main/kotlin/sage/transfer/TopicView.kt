package sage.transfer

import sage.entity.TopicPost
import sage.entity.TopicStat
import java.sql.Timestamp

class TopicView {
  var id: Long = 0
  var title: String = ""
  var content: String = ""
  var reference: String = ""
  var author: UserLabel? = null

  var belongTag: TagLabel? = null
  var tags: List<TagLabel> = arrayListOf()

  var whenCreated: Timestamp? = null
  var whenModified: Timestamp? = null
  var whenLastReplied: Timestamp? = null
  var replyCount: Int = 0

  var likes: Int = 0
  var views: Int = 0

  internal constructor() {
  }

  constructor(topic: TopicPost) {
    id = topic.id
    title = topic.title
    content = topic.content
    reference = topic.reference
    author = UserLabel(topic.author)

    belongTag = TagLabel(topic.belongTag)
    tags = topic.tags.map { TagLabel(it) }

    whenCreated = topic.whenCreated
    whenModified = actualWhenModified(topic.whenCreated, topic.whenModified)
    val stat = topic.stat()
    whenLastReplied = stat.whenLastReplied
    replyCount = topic.maxFloorNumber
    likes = stat.likes
    views = stat.views
  }
}