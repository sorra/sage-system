package sage.entity

import com.avaje.ebean.Ebean
import com.avaje.ebean.Model
import com.avaje.ebean.annotation.Index
import com.avaje.ebean.annotation.WhenModified
import java.sql.Timestamp
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class TweetStat(
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
    var forwards: Int = 0,
    var comments: Int = 0
) : Model() {

  companion object : Find<Long, TweetStat>() {
    fun get(id: Long) = getNonNull(TweetStat::class, id)

    fun like(id: Long, userId: Long) {
      Liking.like(userId, Liking.TWEET, id, TweetStat::class.java, "tweetStat")
      get(id).update()
    }

    fun unlike(id: Long, userId: Long) {
      Liking.unlike(userId, Liking.TWEET, id, TweetStat::class.java, "tweetStat")
      get(id).update()
    }

    fun incForwards(id: Long) {
      Ebean.createUpdate(TweetStat::class.java, "update tweetStat set forwards = forwards+1 where id = :id")
          .setParameter("id", id).execute()
      get(id).update()
    }

    fun incComments(id: Long) {
      Ebean.createUpdate(TweetStat::class.java, "update tweetStat set comments = comments+1 where id = :id")
          .setParameter("id", id).execute()
      get(id).update()
    }
  }
}