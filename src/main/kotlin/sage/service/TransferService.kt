package sage.service

import org.springframework.stereotype.Service
import sage.entity.Liking
import sage.entity.Tweet
import sage.entity.TweetStat
import sage.transfer.TweetView
import sage.web.auth.Auth

@Service
class TransferService {

  fun toTweetView(tweet: Tweet) = TweetView(tweet, Tweet.getOrigin(tweet), tweetStatFinder = tweetStatFinder)

  fun toTweetViewNoCount(tweet: Tweet) = TweetView(tweet, Tweet.getOrigin(tweet))

  @JvmOverloads fun toTweetViews(tweets: Collection<Tweet>, showOrigin: Boolean = true, showCounts: Boolean = true): List<TweetView> {
    return tweets.map { t ->
      val origin = if (showOrigin) Tweet.getOrigin(t) else null
      val finder = if (showCounts) tweetStatFinder else {id -> null}
      TweetView(t, origin, isLikedChecker, finder)
    }
  }

  val isLikedChecker = { id: Long ->
    Auth.uid()?.run { Liking.find(this, Liking.TWEET, id) } != null
  }

  val tweetStatFinder = { id: Long ->
    TweetStat.get(id)
  }
}
