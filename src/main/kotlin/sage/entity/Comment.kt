package sage.entity

import javax.persistence.Entity

@Entity
class Comment : BaseModel {

  var content: String? = null

  var author: User? = null

  var sourceId: Long = 0

  var replyUserId: Long? = null

  @delegate:javax.persistence.Transient
  val source: Tweet? by lazy { load(Tweet::class, sourceId) }

  constructor(content: String, author: User, sourceId: Long, replyUserId: Long?) {
    this.content = content
    this.author = author
    this.sourceId = sourceId
    this.replyUserId = replyUserId
  }

  companion object : Find<Long, Comment>() {
    fun ofTweet(id: Long) = where().eq("sourceId", id).findList()

    fun countOfTweet(id: Long) = where().eq("sourceId", id).findRowCount()
  }
}