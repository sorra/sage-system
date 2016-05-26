package sage.service

import httl.util.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sage.domain.commons.*
import sage.domain.search.SearchBase
import sage.entity.*
import sage.transfer.MidForwards
import sage.transfer.TweetView
import sage.util.Strings
import java.util.*

@Suppress("NAME_SHADOWING")
@Service
class TweetPostService
@Autowired constructor(
    private val searchBase: SearchBase,
    private val userService: UserService,
    private val transfers: TransferService,
    private val notifService: NotificationService) {

  fun post(userId: Long, content: String, tagIds: Collection<Long>): Tweet {
    if (content.isEmpty() || content.length > TWEET_MAX_LEN) {
      throw BAD_TWEET_LENGTH
    }
    val parsedContent = processContent(content)
    val content = parsedContent.content
    val tweet = Tweet(content, User.ref(userId),
        Tag.multiGet(tagIds))
    tweet.save()

    userService.updateUserTag(userId, tagIds)

    parsedContent.mentionedIds.forEach { atId -> notifService.mentionedByTweet(atId, userId, tweet.id) }

    searchBase.index(tweet.id, transfers.toTweetViewNoCount(tweet))
    return tweet
  }

  fun forward(userId: Long, content: String, originId: Long, removedForwardIds: Collection<Long>): Tweet {
    if (content.length > TWEET_MAX_LEN) {
      throw BAD_TWEET_LENGTH
    }
    val parsedContent = processContent(content)
    var content = parsedContent.content
    if (content.isEmpty()) content = " "

    val directOrigin = Tweet.ref(originId)
    val origins = fromDirectToInitialOrigin(directOrigin)
    val initialOrigin = origins.last
    val tweet: Tweet
    if (initialOrigin == directOrigin) {
      tweet = Tweet(content, User.ref(userId), initialOrigin)
    } else {
      val midForwards = MidForwards(directOrigin)
      removedForwardIds.forEach { midForwards.removeById(it) }
      tweet = Tweet(content, User.ref(userId), initialOrigin, midForwards)
    }
    tweet.save()

    userService.updateUserTag(userId, tweet.tags.map { it.id })

    origins.forEach { origin -> notifService.forwarded(origin.author.id, userId, tweet.id) }
    parsedContent.mentionedIds.forEach { atId -> notifService.mentionedByTweet(atId, userId, tweet.id) }

    searchBase.index(tweet.id, transfers.toTweetViewNoCount(tweet))
    return tweet
  }

  fun comment(userId: Long, content: String, sourceId: Long?, replyUserId: Long?): Comment {
    var content = content
    if (content.isEmpty() || content.length > COMMENT_MAX_LEN) {
      throw BAD_COMMENT_LENGTH
    }
    val parsedContent = processContent(content)
    content = parsedContent.content

    val source = Tweet.ref(sourceId)
    val comment = Comment(content, User.ref(userId), sourceId!!, replyUserId)
    comment.save()

    notifService.commented(source.author.id, userId, comment.id)
    if (replyUserId != null) {
      notifService.replied(replyUserId, userId, comment.id)
    }
    parsedContent.mentionedIds.forEach { atId -> notifService.mentionedByComment(atId, userId, comment.id) }
    return comment
  }

  fun share(userId: Long, blog: Blog) {
    val SUM_LEN = 100
    val content = blog.content
    val summary = if (content.length > SUM_LEN) content.substring(0, SUM_LEN) else content
    val tweet = Tweet(
        "发表了博客：[" + blogRef(blog) + "] " + summary,
        User.ref(userId), blog)
    tweet.save()

    searchBase.index(tweet.id, transfers.toTweetViewNoCount(tweet))
  }

  fun delete(userId: Long, tweetId: Long) {
    val tweet = Tweet.ref(tweetId)
    if (!IdCommons.equal(userId, tweet.author.id)) {
      throw DomainException("User[%d] is not the author of Tweet[%d]", userId, tweetId)
    }
    tweet.deleted = true
    tweet.update()
    searchBase.delete(TweetView::class.java, tweetId)
  }

  private fun blogRef(blog: Blog): String {
    return String.format("<a href=\"%s\">%s</a>", "/blogs/" + blog.id, blog.title)
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

  /*
   * Escape HTML and replace mentions
   */
  private fun processContent(content: String): ParsedContent {
    var content = Strings.escapeHtmlTag(content)
    val mentionedIds = HashSet<Long>()
    content = ReplaceMention.with {User.byName(it)}.apply(content, mentionedIds)
    content = Links.linksToHtml(content)

    return ParsedContent(content, mentionedIds)
  }

  private class ParsedContent internal constructor(internal val content: String, internal val mentionedIds: Set<Long>)

  companion object {
    private val TWEET_MAX_LEN = 1000
    private val COMMENT_MAX_LEN = 1000
    private val BAD_TWEET_LENGTH = BadArgumentException("状态应为1~1000字")
    private val BAD_COMMENT_LENGTH = BadArgumentException("评论应为1~1000字")
  }
}
