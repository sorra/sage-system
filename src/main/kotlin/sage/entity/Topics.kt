package sage.entity

import com.avaje.ebean.Ebean
import com.avaje.ebean.ExpressionList
import com.avaje.ebean.TxCallable
import com.avaje.ebean.annotation.Index
import com.avaje.ebean.annotation.SoftDelete
import com.avaje.ebean.annotation.WhenCreated
import sage.domain.commons.DomainException
import java.sql.Connection
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity
class TopicPost(
    var title: String,

    @Column(columnDefinition = "TEXT", length = 65535)
    @Lob @Basic
    var content: String = "",

    var reference: String = "",

    @ManyToOne(optional = false)
    val author: User,

    @ManyToOne
    var belongTag: Tag,

    @ManyToMany(cascade = arrayOf(javax.persistence.CascadeType.ALL))
    var tags: MutableSet<Tag> = HashSet(),

    var maxFloorNumber: Int = 0,
    @WhenCreated @Index
    var whenLastActive: Timestamp? = null
) : BaseModel() {
  @SoftDelete
  var deleted: Boolean = false

  fun stat() = TopicStat.get(id)

  companion object : Find<Long, TopicPost>() {
    fun get(id: Long) = getNonNull(TopicPost::class, id)

    fun nextFloorNumber(id: Long): Int = Ebean.execute(TxCallable {
      get(id).run {
        whenLastActive = Timestamp(System.currentTimeMillis())
        maxFloorNumber += 1
        update()
        maxFloorNumber
      }
    })
  }
}

@Entity
class TopicReply(
    @Column(columnDefinition = "TEXT", length = 65535)
    @Lob @Basic
    var content: String,

    @ManyToOne(optional = false)
    val author: User,

    val topicPostId: Long,

    val toUserId: Long?,

    val toReplyId: Long?,

    val floorNumber: Int
) : BaseModel() {
  @SoftDelete
  var deleted: Boolean = false

  fun topicPost() = TopicPost.get(topicPostId)

  companion object : Find<Long, TopicReply>() {
    fun get(id: Long) = getNonNull(TopicReply::class, id)

    fun ExpressionList<TopicReply>.ofPost(postId: Long) = eq("topicPostId", postId)

    fun byPostId(postId: Long) = where().ofPost(postId).findList()
  }
}