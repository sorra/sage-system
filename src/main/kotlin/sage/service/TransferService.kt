package sage.service

import org.springframework.stereotype.Service
import sage.entity.Liking
import sage.entity.Tweet
import sage.transfer.TweetView
import sage.web.auth.Auth

@Service
class TransferService {

  fun toTweetView(tweet: Tweet) = TweetView(tweet, Tweet.getOrigin(tweet), showsStat = true, isLikedChecker = isLikedChecker)

  fun toTweetViewNoCount(tweet: Tweet) = TweetView(tweet, Tweet.getOrigin(tweet), showsStat = false, isLikedChecker = {false})

  fun toTweetViews(tweets: Collection<Tweet>, showOrigin: Boolean = true, showCounts: Boolean = true): List<TweetView> {
    return tweets.map { t ->
      val origin = if (showOrigin) Tweet.getOrigin(t) else null
      TweetView(t, origin, showsStat = showCounts, isLikedChecker = isLikedChecker)
    }
  }

  val isLikedChecker = { id: Long ->
    Auth.uid()?.let { Liking.find(it, Liking.TWEET, id) } != null
  }
}
