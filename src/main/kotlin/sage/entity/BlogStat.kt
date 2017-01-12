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
class BlogStat (
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
    var comments: Int = 0
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
      var days = Instant.ofEpochMilli(siteLaunchTime).until(whenCreated!!.toInstant(), ChronoUnit.DAYS)
      if (days < 0) days = 0
      floatUp = Math.pow(1.2, days.toDouble())
    }
    rank = (1 + comments + likes + views / 10) * (1 + tune) * floatUp
  }

  companion object : Find<Long, BlogStat>() {
    fun get(id: Long) = getNonNull(BlogStat::class, id)

    fun like(id: Long, userId: Long) {
      Liking.like(userId, Liking.BLOG, id, BlogStat::class.java, "blogStat")
      get(id).update()
      TweetStat.like(Blog.get(id).tweetId, userId)
    }

    fun unlike(id: Long, userId: Long) {
      Liking.unlike(userId, Liking.BLOG, id, BlogStat::class.java, "blogStat")
      get(id).update()
      TweetStat.unlike(Blog.get(id).tweetId, userId)
    }

    fun incComments(id: Long) {
      Ebean.createUpdate(BlogStat::class.java, "update blogStat set comments = comments+1 where id = :id")
          .setParameter("id", id).execute()
      get(id).update()
      TweetStat.incComments(Blog.get(id).tweetId)
    }

    fun incViews(id: Long) {
      Ebean.createUpdate(BlogStat::class.java, "update blogStat set views = views+1 where id = :id")
          .setParameter("id", id).execute()
      byId(id)?.apply {
        if (views % 10 == 0) update()
      }
    }
  }
}