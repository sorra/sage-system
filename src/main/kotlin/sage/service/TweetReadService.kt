package sage.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sage.domain.commons.Edge
import sage.entity.*
import sage.transfer.FollowListLite
import sage.transfer.TweetView

@Service
class TweetReadService
@Autowired constructor(private val transfers: TransferService) {

  fun byFollowings(userId: Long, edge: Edge): List<Tweet> {
    val tweets = Tweet.byAuthor(userId, edge)

    // Find and merge tweets from followings
    val followings = Follow.followings(userId)
    followings.forEach { f -> tweets.addAll(byFollow(f, edge)) }
    tweets.sortByDescending { it.whenCreated }

    // Select the top items, for later's higher sort
    return tweets.take(Edge.FETCH_SIZE)
  }

  private fun byFollow(follow: Follow, edge: Edge): List<Tweet> {
    val authorId = follow.target.id
    val result: List<Tweet>
    if (follow.isIncludeAll) {
      result = Tweet.byAuthor(authorId, edge)
    } else if (follow.isIncludeNew) {
      updateOffsetIfNeeded(follow)
      result = Tweet.byAuthorAndTags(authorId, follow.tags, edge)
    } else {
      result = Tweet.byAuthorAndTags(authorId, follow.tags, edge)
    }
    return result
  }

  private fun updateOffsetIfNeeded(follow: Follow) {
    try {
      val newOffset = UserTag.lastIdByUser(follow.target.id)
      val oldOffset = follow.userTagOffset
      if (oldOffset < newOffset) {
        val coveredTags = Tag.getQueryTags(follow.tags)
        val newTags = Tag.multiGet(UserTag.byUserAndAfterId(follow.target.id, oldOffset).map { it.id })

        val pureNewTags = newTags.filter { !coveredTags.contains(it) }.toSet()
        if (pureNewTags.isNotEmpty()) {
          follow.tags.addAll(pureNewTags)
          follow.userTagOffset = newOffset
          follow.update()
        }
      }
    } catch (e: Exception) {
      log.error("updateOffsetIfNeeded encounters error, but we skip.", e)
    }

  }

  fun byFollowListLite(list: FollowListLite, edge: Edge): List<Tweet> =
      list.list.flatMap { info -> Tweet.byAuthorAndTags(info.userId!!, Tag.multiGet(info.tagIds), edge) }

  fun byTag(tagId: Long, edge: Edge): List<Tweet> = Tweet.byTags(setOf(Tag.ref(tagId)), edge)

  fun byAuthor(authorId: Long, edge: Edge): List<Tweet> = Tweet.byAuthor(authorId, edge)

  fun getTweetView(tweetId: Long): TweetView? =
      Tweet.byId(tweetId)?.run { transfers.toTweetView(this) }

  fun getForwards(originId: Long): Collection<Tweet> {
    val tweet = Tweet.byId(originId)
    if (tweet == null || tweet.deleted) {
      return emptyList()
    }
    return Tweet.forwards(originId)
  }

  fun getComments(sourceId: Long): Collection<Comment> {
    val tweet = Tweet.byId(sourceId)
    if (tweet == null || tweet.deleted) {
      return emptyList()
    }
    return Comment.ofTweet(sourceId)
  }

  /**
   * Experimental

   * @return a sequential list of connected tweets
   */
  fun connectTweets(blogId: Long) = Tweet.connectTweets(blogId).map { transfers.toTweetView(it) }

  companion object {
    private val log = LoggerFactory.getLogger(TweetReadService::class.java)
  }
}
