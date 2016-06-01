package sage.entity

import com.avaje.ebean.Ebean
import com.avaje.ebean.Model
import com.avaje.ebean.annotation.Index
import com.avaje.ebean.annotation.WhenModified
import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class TopicStat (
    @Id
    var id: Long = 0,
    var whenCreated: Timestamp? = null,
    @WhenModified
    var whenModified: Timestamp? = null,
    @Index
    var rank: Double = 0.0,
    var floatUp: Double = 0.0,
    var tune: Int = 0,
    var likes: Int = 0,
    var views: Int = 0,
    var whenLastReplied: Timestamp? = null,
    var replies: Int = 0
) : Model() {
  override fun save() {
    computeRank()
    super.save()
  }

  fun computeRank() = apply {
    if (whenCreated == null) return@apply
    if(floatUp == 0.0) {
      var days = Instant.ofEpochMilli(siteLaunchTime).until(whenCreated!!.toInstant(), ChronoUnit.DAYS)
      if (days < 0) days = 0
      floatUp = Math.pow(1.25, days.toDouble())
    }
    rank = (1 + tune + likes + replies + views / 10) * floatUp
  }

  companion object : Find<Long, TopicStat>() {
    fun get(id: Long) = getNonNull(TopicStat::class, id)

    fun like(id: Long, userId: Long) {
      if (Liking.find(userId, Liking.TOPIC, id) == null) {
        Liking(userId, Liking.TOPIC, id).save()
        Ebean.createUpdate(TopicStat::class.java, "update topicStat set likes = likes+1 where id = :id")
            .setParameter("id", id).execute()
      }
    }

    fun unlike(id: Long, userId: Long) {
      Liking.find(userId, Liking.TOPIC, id)?.apply {
        delete()
        Ebean.createUpdate(TopicStat::class.java, "update topicStat set likes = likes-1 where id = :id")
            .setParameter("id", id).execute()
      }
    }

    fun incViews(id: Long) {
      Ebean.createUpdate(TopicStat::class.java, "update topicStat set views = views+1 where id = :id")
          .setParameter("id", id).execute()
      byId(id)?.apply {
        if (views % 10 == 0) computeRank().update()
      }
    }
  }
}