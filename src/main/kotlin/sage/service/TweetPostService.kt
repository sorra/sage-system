package sage.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sage.domain.commons.*
import sage.entity.*
import sage.transfer.MidForwards
import sage.transfer.TweetView
import java.util.*

@Service
class TweetPostService
@Autowired constructor(
    private val searchService: SearchService,
    private val userService: UserService,
    private val transfers: TransferService,
    private val notifService: NotificationService) {

  fun post(userId: Long, content: String, tagIds: Collection<Long>): Tweet {
    if (content.isEmpty() || content.length > TWEET_MAX_LEN) {
      throw BAD_TWEET_LENGTH
    }
    val (richContent, mentionedIds) = ContentParser.tweet(content) { name -> User.byName(name) }

    val tweet = Tweet(richContent, User.ref(userId),
        Tag.multiGet(tagIds))
    tweet.save()

    userService.updateUserTag(userId, tagIds)

    mentionedIds.forEach { atId -> notifService.mentionedByTweet(atId, userId, tweet.id) }

    searchService.index(tweet.id, transfers.toTweetViewNoCount(tweet))
    return tweet
  }

  fun forward(userId: Long, content: String, originId: Long, removedForwardIds: Collection<Long>): Tweet {
    if (content.length > TWEET_MAX_LEN) {
      throw BAD_TWEET_LENGTH
    }
    val (richContent, mentionedIds) = ContentParser.tweet(content) { name -> User.byName(name) }

    val directOrigin = Tweet.ref(originId)
    val origins = fromDirectToInitialOrigin(directOrigin)
    val initialOrigin = origins.last
    val tweet: Tweet
    if (initialOrigin == directOrigin) {
      tweet = Tweet(richContent, User.ref(userId), initialOrigin)
    } else {
      val midForwards = MidForwards(directOrigin)
      removedForwardIds.forEach { midForwards.removeById(it) }
      tweet = Tweet(richContent, User.ref(userId), initialOrigin, midForwards)
    }
    tweet.save()

    userService.updateUserTag(userId, tweet.tags.map { it.id })

    origins.forEach { origin -> notifService.forwarded(origin.author.id, userId, tweet.id) }
    mentionedIds.forEach { atId -> notifService.mentionedByTweet(atId, userId, tweet.id) }

    searchService.index(tweet.id, transfers.toTweetViewNoCount(tweet))
    return tweet
  }

  fun comment(userId: Long, content: String, sourceId: Long, replyUserId: Long?): Comment {
    if (content.isEmpty() || content.length > COMMENT_MAX_LEN) {
      throw BAD_COMMENT_LENGTH
    }
    val (richContent, mentionedIds) = ContentParser.tweet(content) { name -> User.byName(name) }

    val comment = Comment(richContent, User.ref(userId), Comment.TWEET, sourceId, replyUserId)
    comment.save()

    notifService.commentedTweet(Tweet.ref(sourceId).author.id, userId, comment.id)
    if (replyUserId != null) {
      notifService.replied(replyUserId, userId, comment.id)
    }
    mentionedIds.forEach { atId -> notifService.mentionedByComment(atId, userId, comment.id) }
    return comment
  }

  fun delete(userId: Long, tweetId: Long) {
    val tweet = Tweet.ref(tweetId)
    if (!IdCommons.equal(userId, tweet.author.id)) {
      throw DomainException("User[%d] is not the author of Tweet[%d]", userId, tweetId)
    }
    tweet.deleted = true
    tweet.update()
    searchService.delete(TweetView::class.java, tweetId)
  }

  /*
   * Find all nested origins including the direct origin
   */
  private fun fromDirectToInitialOrigin(directOrigin: Tweet): Deque<Tweet> {
    val origins = LinkedList<Tweet>()
    origins.add(directOrigin)
    var origin = Tweet.getOrigin(directOrigin)
    while (origin != null) {
      origins.add(origin)
      origin = Tweet.getOrigin(origin)
    }
    return origins
  }

  companion object {
    private val TWEET_MAX_LEN = 1000
    private val COMMENT_MAX_LEN = 1000
    private val BAD_TWEET_LENGTH = BadArgumentException("状态应为1~1000字")
    private val BAD_COMMENT_LENGTH = BadArgumentException("评论应为1~1000字")
  }
}
