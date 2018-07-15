package sage.domain.constraints

import sage.domain.commons.BadArgumentException
import sage.entity.Tweet


object TweetConstraints {
  private const val TWEET_MAX_LEN = 1000

  fun check(tweet: Tweet) {
    if (!tweet.hasOrigin() && !tweet.hasBlog() && tweet.inputContent.isBlank()) {
      throw BadArgumentException("微言字数为0")
    }

    if (tweet.inputContent.length > TWEET_MAX_LEN) {
      throw BadArgumentException("微言为${tweet.inputContent.length}字，不能超过${TWEET_MAX_LEN}字")
    }
  }
}