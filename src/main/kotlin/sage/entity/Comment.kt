package sage.entity

import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class Comment : BaseModel {

  var content: String = ""

  @ManyToOne(optional = false)
  var author: User

  var sourceType: Short = 0

  var sourceId: Long = 0

  var replyUserId: Long? = null

  constructor(content: String, author: User, sourceType: Short, sourceId: Long, replyUserId: Long?) {
    this.content = content
    this.author = author
    this.sourceType = sourceType
    this.sourceId = sourceId
    this.replyUserId = replyUserId
  }

  companion object : Find<Long, Comment>() {
    val BLOG: Short = 1
    val TWEET: Short = 2

    fun byBlog(id: Long) = where().eq("sourceType", BLOG).eq("sourceId", id).findList()
    fun countByBlog(id: Long) = where().eq("sourceType", BLOG).eq("sourceId", id).findRowCount()

    fun byTweet(id: Long) = where().eq("sourceType", TWEET).eq("sourceId", id).findList()
    fun countByTweet(id: Long) = where().eq("sourceType", TWEET).eq("sourceId", id).findRowCount()
  }
}