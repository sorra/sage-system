package sage.service

import com.avaje.ebean.Ebean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sage.domain.cache.GlobalCaches
import sage.domain.commons.ContentParser
import sage.domain.commons.RichElement
import sage.domain.intelligence.TagAnalyzer
import sage.domain.permission.TweetPermission
import sage.entity.*
import sage.transfer.MidForwards
import java.util.*

@Service
class TweetPostService
@Autowired constructor(
    private val searchService: SearchService,
    private val userService: UserService,
    private val notifService: NotificationService) {

  fun post(userId: Long, inputContent: String, richElements: Collection<RichElement>, tagIds: Collection<Long>): Tweet {
    val intelliTagIds = TagAnalyzer(inputContent, tagIds, userService.topTags(userId).map { it.id }).analyze()

    val (hyperContent, mentionedIds) = ContentParser.tweet(inputContent) { name -> User.byName(name) }

    val tweet = Tweet(inputContent, hyperContent, richElements, User.ref(userId), Tag.multiGet(intelliTagIds))

    tweet.validate()

    Ebean.execute {
      tweet.save()
      TweetStat(id = tweet.id, whenCreated = tweet.whenCreated).save()
    }

    userService.updateUserTag(userId, intelliTagIds)

    mentionedIds.forEach { atId -> notifService.mentionedByTweet(atId, userId, tweet.id) }

    index(tweet)
    GlobalCaches.tweetsCache.clear()
    return tweet
  }

  /** Should be called in a transaction */
  fun postForBlog(blog: Blog) {
    val tweet = Tweet("", "", emptyList(), User.ref(blog.author.id), blog)

    tweet.validate()

    tweet.save()
    TweetStat(id = tweet.id, whenCreated = tweet.whenCreated).save()
    blog.tweetId = tweet.id
    blog.update()
  }

  fun forward(userId: Long, inputContent: String, originId: Long, removedForwardIds: Collection<Long>): Tweet {
    val (hyperContent, mentionedIds) = ContentParser.tweet(inputContent) { name -> User.byName(name) }

    val directOrigin = Tweet.ref(originId)
    val origins = fromDirectToInitialOrigin(directOrigin)
    val initialOrigin = origins.last

    val tweet: Tweet =
        if (initialOrigin == directOrigin) {
          Tweet(inputContent, hyperContent, emptyList(), User.ref(userId), initialOrigin)
        } else {
          val midForwards = MidForwards(directOrigin)
          removedForwardIds.forEach { midForwards.removeById(it) }
          Tweet(inputContent, hyperContent, emptyList(), midForwards, User.ref(userId), initialOrigin)
        }

    tweet.validate()

    Ebean.execute {
      tweet.save()
      TweetStat(id = tweet.id, whenCreated = tweet.whenCreated).save()
    }
    origins.forEach { TweetStat.incForwards(it.id) }

    userService.updateUserTag(userId, tweet.tags.map(Tag::id))

    origins.forEach { origin -> notifService.forwarded(origin.author.id, userId, tweet.id) }
    mentionedIds.forEach { atId -> notifService.mentionedByTweet(atId, userId, tweet.id) }

    index(tweet)
    GlobalCaches.tweetsCache.clear()
    return tweet
  }

  fun comment(userId: Long, inputContent: String, tweetId: Long, replyUserId: Long?): Comment {
    val (hyperContent, mentionedIds) = ContentParser.comment(inputContent) { name -> User.byName(name) }

    val comment = Comment(inputContent, hyperContent, User.ref(userId), Comment.TWEET, tweetId, replyUserId)

    comment.validate()

    comment.save()
    TweetStat.incComments(tweetId)
    Tweet.byId(tweetId)?.let(Tweet::blogId)?.let { if (it > 0) {
      BlogStat.incComments(it)
    }}

    if (replyUserId != null) {
      notifService.replied(replyUserId, userId, comment.id)
    }
    val tweetAuthorId = Tweet.ref(tweetId).author.id
    if (replyUserId != tweetAuthorId) {
      // Avoid REPLIED and COMMENTED notifications being duplicate
      notifService.commented(tweetAuthorId, userId, comment.id)
    }
    mentionedIds.forEach { atId -> notifService.mentionedByComment(atId, userId, comment.id) }

    return comment
  }

  fun delete(userId: Long, tweetId: Long) {
    val tweet = Tweet.get(tweetId)

    TweetPermission(userId, tweet).canDelete()

    tweet.deleted = true
    tweet.update()

    unindex(tweet)
    GlobalCaches.tweetsCache.clear()
  }

  fun reindexAll() {
    Tweet.where().setIncludeSoftDeletes().findEach {
      if (it.deleted) {
        unindex(it)
      } else {
        index(it)
      }
    }
  }

  private fun index(tweet: Tweet) {
    searchService.index(SearchService.TWEET, tweet.id, tweet.toSearchableTweet())
  }

  private fun unindex(tweet: Tweet) {
    searchService.delete(SearchService.TWEET, tweet.id)
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
}
