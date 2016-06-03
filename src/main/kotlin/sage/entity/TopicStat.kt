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
  override fun update() {
    computeRank()
    super.update()
  }

  fun computeRank() = apply {
    if (whenCreated == null) return@apply
    if(floatUp == 0.0) {
      val daysCr = Instant.ofEpochMilli(siteLaunchTime).until(whenCreated!!.toInstant(), ChronoUnit.DAYS)
      val daysRe = whenLastReplied?.run { Instant.ofEpochMilli(siteLaunchTime).until(toInstant(), ChronoUnit.DAYS) } ?: 0
      val days =
        if (daysCr == 0L) {
          0.0
        } else if (daysRe > 0) {
          daysCr * 0.2 + daysRe * 0.8
        } else {
          daysCr.toDouble()
        }
      floatUp = Math.pow(1.25, days)
    }
    rank = (1 + tune + replies + likes + views / 10) * floatUp
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
        if (views % 10 == 0) update()
      }
    }
  }
}