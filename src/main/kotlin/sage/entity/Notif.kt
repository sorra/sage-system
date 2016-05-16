package sage.entity

import javax.persistence.Entity

@Entity
class Notif : BaseModel {

  var ownerId: Long? = null
    private set
  var senderId: Long? = null
    private set
  var type: Type? = null
    private set
  var sourceId: Long? = null
    private set

  constructor(ownerId: Long?, senderId: Long?, type: Type, sourceId: Long?) {
    this.ownerId = ownerId
    this.senderId = senderId
    this.type = type
    this.sourceId = sourceId
  }

  companion object : Find<Long, Notif>() {
    fun byOwner(ownerId: Long) = where().eq("ownerId", ownerId).findList()
    fun byOwnerAndAfterId(ownerId: Long, notifId: Long) =
        where().eq("ownerId", ownerId).gt("id", notifId).orderBy("id desc").findList()
  }

  enum class Type private constructor(val sourceType: SourceType, val desc: String, val shortDesc: String) {
    FOLLOWED(SourceType.USER, "关注了你", "新粉丝"),
    FORWARDED(SourceType.TWEET, "转发了你的微博", "转发"),
    COMMENTED(SourceType.COMMENT, "评论了你的微博", "评论"),
    REPLIED(SourceType.COMMENT, "回复了你", "回复"),
    MENTIONED_TWEET(SourceType.TWEET, "在微博中提到了你", "微博@"),
    MENTIONED_COMMENT(SourceType.COMMENT, "在评论中提到了你", "评论@"),
    MENTIONED_TOPIC_POST(SourceType.TOPIC_POST, "在帖子中提到了你", "帖子@"),
    MENTIONED_TOPIC_REPLY(SourceType.TOPIC_REPLY, "在帖子中提到了你", "帖子@"),
    REPIED_IN_TOPIC(SourceType.TOPIC_REPLY, "在帖子中回复了你", "帖子回复")
  }

  enum class SourceType {
    USER, TWEET, COMMENT, TOPIC_POST, TOPIC_REPLY
  }
}