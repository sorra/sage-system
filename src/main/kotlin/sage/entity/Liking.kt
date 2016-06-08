package sage.entity

import com.avaje.ebean.Ebean
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

    fun find(userId: Long, likeType: Short, likeId: Long) =
        where().eq("userId", userId).eq("likeType", likeType).eq("likeId", likeId).findUnique()

    fun like(userId: Long, likeType: Short, likeId: Long, statBeanType: Class<*>, statName: String) = Ebean.execute {
      if (Liking.find(userId, likeType, likeId) == null) {
        Liking(userId, likeType, likeId).save()
        Ebean.createUpdate(statBeanType, "update $statName set likes = likes+1 where id = :id")
            .setParameter("id", likeId)
            .execute()
      }
    }

    fun unlike(userId: Long, likeType: Short, likeId: Long, statBeanType: Class<*>, statName: String) = Ebean.execute {
      val deleted = Ebean.createUpdate(Liking::class.java, "delete from liking where userId = :userId and likeType = :likeType and likeId = :likeId")
          .setParameter("userId", userId).setParameter("likeType", likeType).setParameter("likeId", likeId)
          .execute()
      if (deleted > 0) {
        Ebean.createUpdate(statBeanType, "update $statName set likes = likes-1 where id = :id")
            .setParameter("id", likeId)
            .execute()
      }
    }

  }
}