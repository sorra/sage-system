package sage.entity

import com.avaje.ebean.Model
import com.avaje.ebean.annotation.Index
import com.avaje.ebean.annotation.WhenCreated
import java.sql.Timestamp
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
@Index(columnNames = arrayOf("user_id", "like_type", "like_id"), unique = true)
class Liking(
    var userId: Long,
    var likeType: Short,
    var likeId: Long
) : Model() {
  @Id @GeneratedValue
  var id: Long = 0
  @WhenCreated
  var whenCreated: Timestamp? = null

  companion object : Find<Long, Liking>() {
    val BLOG: Short = 1
    val TOPIC: Short = 2

    fun find(userId: Long, likedType: Short, likedId: Long) =
        where().eq("userId", userId).eq("likeType", likedType).eq("likeId", likedId).findUnique()
  }
}