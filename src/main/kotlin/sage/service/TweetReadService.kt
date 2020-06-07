package sage.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sage.domain.commons.Edge
import sage.entity.Follow
import sage.entity.Tag
import sage.entity.Tweet
import sage.entity.UserTag
import sage.transfer.TweetView

@Service
class TweetReadService
@Autowired constructor(private val transfers: TransferService,
                       private val searchService: SearchService) {

  fun byFollowings(userId: Long, edge: Edge): List<Tweet> {
    val tweets = Tweet.byAuthor(userId, edge)

    // Find and merge tweets from followings
    val followings = Follow.followings(userId)
    followings.flatMapTo(tweets) { f -> byFollow(f, edge) }

    tweets.sortByDescending { it.whenCreated }
    // Select the top items, for later's higher sort
    return tweets.take(edge.limitCount)
  }

  private fun byFollow(follow: Follow, edge: Edge): List<Tweet> {
    val authorId = follow.target.id
    return when {
      follow.isIncludeAll -> {
        Tweet.byAuthor(authorId, edge)
      }
      follow.isIncludeNew -> {
        updateOffsetIfNeeded(follow)
        Tweet.byAuthorAndTags(authorId, follow.tags, edge)
      }
      else -> {
        Tweet.byAuthorAndTags(authorId, follow.tags, edge)
      }
    }
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

  fun byTag(tagId: Long, edge: Edge): List<Tweet> = Tweet.byTags(setOf(Tag.ref(tagId)), edge)

  fun byAuthor(authorId: Long, edge: Edge): List<Tweet> = Tweet.byAuthor(authorId, edge)

  fun getTweetView(tweetId: Long): TweetView? =
      Tweet.byId(tweetId)?.let(transfers::toTweetView)

  fun getForwards(originId: Long): Collection<Tweet> {
    val tweet = Tweet.byId(originId)
    if (tweet == null || tweet.deleted) {
      return emptyList()
    }
    return Tweet.forwards(originId)
  }

  fun search(query: String): List<TweetView> {
    val searchHits = searchService.search(SearchService.TWEET, query).hits

    return searchHits.hits.mapNotNull {
      val id = it.id.toLong()
      Tweet.byId(id)
    }.map(transfers::toTweetView)
  }

  companion object {
    private val log = LoggerFactory.getLogger(TweetReadService::class.java)
  }
}
