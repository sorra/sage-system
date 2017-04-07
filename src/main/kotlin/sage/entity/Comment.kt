package sage.entity

import sage.domain.constraints.CommentConstraints
import javax.persistence.*

@Entity
class Comment : BaseModel {

  @Column(columnDefinition = "TEXT")
  @Lob
  val inputContent: String

  @Column(columnDefinition = "TEXT")
  @Lob @Basic
  val content: String

  @ManyToOne(optional = false)
  val author: User

  var sourceType: Short

  var sourceId: Long

  val replyUserId: Long?

  constructor(inputContent: String, hyperContent: String, author: User, sourceType: Short, sourceId: Long, replyUserId: Long?) {
    this.inputContent = inputContent
    this.content = hyperContent
    this.author = author
    this.sourceType = sourceType
    this.sourceId = sourceId
    this.replyUserId = replyUserId
  }

  @PrePersist @PreUpdate
  fun validate() {
    CommentConstraints.check(inputContent)
  }

  companion object : Find<Long, Comment>() {
    val BLOG: Short = 1
    val TWEET: Short = 2

    fun list(sourceType: Short, sourceId: Long) =
        if ((1..2).contains(sourceType)) where().eq("sourceType", sourceType).eq("sourceId", sourceId).findList()
        else emptyList()

    fun count(sourceType: Short, sourceId: Long) =
        if ((1..2).contains(sourceType)) where().eq("sourceType", sourceType).eq("sourceId", sourceId).findRowCount()
        else 0
  }
}