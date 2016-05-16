package sage.service

import org.springframework.stereotype.Service
import sage.entity.Comment
import sage.entity.Tweet
import sage.transfer.TweetView
import java.util.*

@Service
class TransferService {

  fun toTweetView(tweet: Tweet): TweetView {
    return TweetView(tweet, Tweet.getOrigin(tweet),
        forwardCount(tweet.id),
        commentCount(tweet.id))
  }

  fun toTweetViewNoCount(tweet: Tweet): TweetView {
    return TweetView(tweet, Tweet.getOrigin(tweet), 0, 0)
  }

  @JvmOverloads fun toTweetViews(tweets: Collection<Tweet>, showOrigin: Boolean = true, showCounts: Boolean = true): List<TweetView> {
    val tcs = ArrayList<TweetView>(20)
    for (t in tweets) {
      val origin = if (showOrigin) Tweet.getOrigin(t) else null
      val forwardCount = if (showCounts) forwardCount(t.id) else 0
      val commentCount = if (showCounts) commentCount(t.id) else 0
      tcs.add(TweetView(t, origin, forwardCount, commentCount))
    }
    return tcs
  }

  fun forwardCount(originId: Long) = Tweet.forwardCount(originId)

  fun commentCount(sourceId: Long) = Comment.countOfTweet(sourceId)
}
