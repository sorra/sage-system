package sage.entity

import sage.domain.constraints.CommentConstraints
import javax.persistence.*

@Entity
class Comment(
    @Column(columnDefinition = "TEXT")
    @Lob val inputContent: String,

    hyperContent: String,

    @ManyToOne(optional = false) val author: User,

    var sourceType: Short,

    var sourceId: Long,

    val replyUserId: Long?
) : BaseModel() {

  @Column(columnDefinition = "TEXT")
  @Lob @Basic
  val content: String = hyperContent

  fun validate() {
    CommentConstraints.check(inputContent)
  }

  companion object : Find<Long, Comment>() {
    val BLOG: Short = 1
    val TWEET: Short = 2

    fun list(sourceType: Short, sourceId: Long): List<Comment> =
        if ((1..2).contains(sourceType)) where().eq("sourceType", sourceType).eq("sourceId", sourceId).findList()
        else emptyList()

    fun count(sourceType: Short, sourceId: Long) =
        if ((1..2).contains(sourceType)) where().eq("sourceType", sourceType).eq("sourceId", sourceId).findRowCount()
        else 0
  }
}