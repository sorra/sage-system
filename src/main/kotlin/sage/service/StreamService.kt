package sage.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sage.domain.commons.Edge
import sage.domain.commons.IdCommons
import sage.entity.Tag
import sage.transfer.*
import java.util.*

@Service
class StreamService
@Autowired constructor(
    private val tweetRead: TweetReadService,
    private val transfers: TransferService,
    private val heedService: HeedService) {

  fun istream(userId: Long, edge: Edge): Stream {
    // Deduplicate
    val mergedSet = HashSet<TweetView>()
    // TweetsByFollowings must be added first, they are prior
    mergedSet.addAll(transfers.toTweetViews(tweetRead.byFollowings(userId, edge)))
    mergedSet.addAll(tcsByTagHeeds(userId, edge))
    mergedSet.addAll(tcsByFollowListHeeds(userId, edge))
    // Multi-source may break the edge assumption:
    // e.g. from followings ends at 33, from heeds ends at 29.
    // Next time should be before 33 or before 29?

    return Stream(higherSort(mergedSet, edge))
  }

  fun istreamByTag(userId: Long, tagId: Long, edge: Edge): Stream {
    val tag = Tag.get(tagId)
    val qtagIds = tag.descendants().map(Tag::id).toSet()

    val originalLimit = edge.limitCount
    edge.limitCount *= 2
    var istream: Stream
    val itemsByTag = mutableListOf<Item>()
    do {
      istream = istream(userId, edge)
      itemsByTag.addAll(filterByQueryTagIds(istream, qtagIds, edge))
      edge.limitStart += edge.limitCount
    // 如果滤后列表不够 而滤前列表还能往后翻页:
    } while (itemsByTag.size < originalLimit && istream.items.size == edge.limitCount)

    return Stream(itemsByTag.take(originalLimit))
  }

  private fun filterByQueryTagIds(istream: Stream, qtagIds: Set<Long>, edge: Edge): List<Item> {
    return istream.items.filter { item -> item.tags.any { qtagIds.contains(it.id) } }.take(edge.limitCount)
  }

  private fun tcsByTagHeeds(userId: Long, edge: Edge): List<TweetView> {
    val tcsByTags = ArrayList<TweetView>()
    for (hd in heedService.tagHeeds(userId)) {
      val tagId = hd.tag.id
      val tagTcs = transfers.toTweetViews(tweetRead.byTag(tagId, edge))
      for (t in tagTcs) {
        t.beFromTag(tagId)
      }
      tcsByTags.addAll(tagTcs)
    }
    return tcsByTags
  }

  private fun tcsByFollowListHeeds(userId: Long, edge: Edge): List<TweetView> {
    val tcsByFollowLists = ArrayList<TweetView>()
    for (hd in heedService.followListHeeds(userId)) {
      val followList = FollowListLite.fromEntity(hd.list)
      tcsByFollowLists.addAll(transfers.toTweetViews(tweetRead.byFollowListLite(followList, edge)))
    }
    return tcsByFollowLists
  }

  fun tagStream(tagId: Long, edge: Edge): Stream {
    val tweets = tweetRead.byTag(tagId, edge)
    return Stream(higherSort(transfers.toTweetViews(tweets), edge))
  }

  fun personalStream(userId: Long, edge: Edge): Stream {
    val tweets = tweetRead.byAuthor(userId, edge)
    return Stream(higherSort(transfers.toTweetViews(tweets), edge))
  }

  fun higherSort(tweets: Collection<TweetView>, edge: Edge): List<Item> {
    val simpleSorted = tweets.sortedByDescending { it.time }.take(edge.limitCount)
    return combineToGroups(simpleSorted)
    // TODO Pull-near
  }

  private fun combineToGroups(tweets: List<TweetView>): List<TweetGroup> {
    val groups = arrayListOf<TweetGroup>()
    fun findExistingGroup(id: Long) = groups.firstOrNull { IdCommons.equal(it.origin?.id, id) }

    for (t in tweets) {
      if (t.origin != null) {
        val existing = findExistingGroup(t.origin!!.id)
        if (existing != null) existing.addForward(t)
        else groups += TweetGroup.createByForward(t)
      } else {
        val existing = findExistingGroup(t.id)
        if (existing != null) existing.addOrigin(t)
        else groups += TweetGroup.createByOrigin(t)
      }
    }

    return groups
  }
}
