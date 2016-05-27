package sage.transfer

import java.util.Date

class HotTopic : Comparable<HotTopic> {
  var id: Long = 0
  var topic: TopicPreview? = null
  var replyCount: Int = 0
  var lastActiveTime: Date? = null
  var rank: Double = 0.0

  internal constructor() {
  }

  constructor(topic: TopicPreview, replyCount: Int, lastReplyTime: Date?) {
    id = topic.id
    this.topic = topic
    this.replyCount = replyCount
    lastActiveTime = lastReplyTime ?: topic.whenCreated
  }

  override fun compareTo(other: HotTopic): Int {
    return -java.lang.Double.compare(rank, other.rank)
  }
}
