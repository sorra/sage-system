package sage.entity

import javax.persistence.*

@Entity
class Comment : BaseModel {

  @Column(columnDefinition = "TEXT")
  @Lob @Basic
  val content: String

  @ManyToOne(optional = false)
  val author: User

  val sourceType: Short

  val sourceId: Long

  val replyUserId: Long?

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

    fun list(sourceType: Short, sourceId: Long) =
        if (sourceType >= 1 && sourceType <= 2) where().eq("sourceType", sourceType).eq("sourceId", sourceId).findList()
        else emptyList()

    fun byBlog(id: Long) = where().eq("sourceType", BLOG).eq("sourceId", id).findList()
    fun byTweet(id: Long) = where().eq("sourceType", TWEET).eq("sourceId", id).findList()
    fun countByTweet(id: Long) = where().eq("sourceType", TWEET).eq("sourceId", id).findRowCount()
  }
}