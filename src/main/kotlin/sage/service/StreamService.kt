package sage.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.Assert
import sage.domain.commons.Edge
import sage.domain.commons.IdCommons
import sage.entity.Tag
import sage.entity.Tweet
import sage.transfer.*
import java.util.*

@Service
class StreamService
@Autowired constructor(
    private val tweetRead: TweetReadService,
    private val transfers: TransferService,
    private val heed: HeedService) {

  @JvmOverloads fun istream(userId: Long, edge: Edge = Edge.none()): Stream {
    // Deduplicate
    val mergedSet = HashSet<TweetView>()
    // TweetsByFollowings must be added first, they are prior
    mergedSet.addAll(transfers.toTweetViews(tweetRead.byFollowings(userId, edge)))
    mergedSet.addAll(tcsByTagHeeds(userId, edge))
    // TODO Receive from group heeds
    mergedSet.addAll(tcsByFollowListHeeds(userId, edge))

    return Stream(higherSort(naiveSortViews(mergedSet)))
  }

  fun istreamByTag(userId: Long, tagId: Long, edge: Edge): Stream {
    val tag = Tag.get(tagId)
    val qtagIds = tag.descendants().map { it.id }.toSet()

    val pageSize = edge.limitCount * 2
    edge.limitCount = pageSize
    var istream = istream(userId, edge)
    val itemsByTag = filterByQueryTagIds(istream, qtagIds) as MutableList
    //如果滤后列表不够 而滤前列表还能往后翻页
    while (itemsByTag.size < Edge.FETCH_SIZE && istream.items.size == pageSize) {
      edge.limitStart += pageSize
      istream = istream(userId, edge)
      itemsByTag.addAll(filterByQueryTagIds(istream, qtagIds))
    }
    return Stream(itemsByTag.take(Edge.FETCH_SIZE))
  }

  private fun filterByQueryTagIds(istream: Stream, qtagIds: Set<Long>): List<Item> {
    return istream.items.filter { item -> item.tags.any { qtagIds.contains(it.id) } }.take(Edge.FETCH_SIZE)
  }

  private fun tcsByTagHeeds(userId: Long, edge: Edge): List<TweetView> {
    val tcsByTags = ArrayList<TweetView>()
    for (hd in heed.tagHeeds(userId)) {
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
    for (hd in heed.followListHeeds(userId)) {
      val followList = FollowListLite.fromEntity(hd.list)
      tcsByFollowLists.addAll(transfers.toTweetViews(tweetRead.byFollowListLite(followList, edge)))
    }
    return tcsByFollowLists
  }

  fun tagStream(tagId: Long, edge: Edge): Stream {
    val tweets = tweetRead.byTag(tagId, edge)
    return Stream(naiveSort(tweets))
  }

  fun personalStream(userId: Long, edge: Edge): Stream {
    val tweets = tweetRead.byAuthor(userId, edge)
    return Stream(naiveSort(tweets))
  }

  private fun naiveSort(tweets: Collection<Tweet>) = naiveSortViews(transfers.toTweetViews(tweets))

  private fun naiveSortViews(tcs: Collection<TweetView>) = tcs.sortedByDescending { it.time }

  private fun higherSort(tcs: List<TweetView>): List<Item> {
    // TODO Pull-near
    return combine(tcs)
  }

  private fun combine(tcs: List<TweetView>): List<Item> {
    val groupSeq = ArrayList<CombineGroup>()
    for (tc in tcs) {
      if (tc.origin != null) {
        val originId = tc.origin.id!!
        val foundGroup = findInSeq(originId, groupSeq)
        if (foundGroup != null) {
          foundGroup.addForward(tc)
        } else {
          groupSeq.add(CombineGroup.newByFirst(tc))
        }
      } else {
        val foundGroup = findInSeq(tc.id!!, groupSeq)
        if (foundGroup != null) {
          foundGroup.addOrigin(tc)
        } else {
          groupSeq.add(CombineGroup.newByOrigin(tc))
        }
      }
    }

    val sequence = ArrayList<Item>(groupSeq.size)
    for (group in groupSeq) {
      if (group.forwards.isEmpty()) {
        Assert.notNull(group.origin)
        sequence.add(group.origin)
      } else
        sequence.add(group)
    }
    return sequence
  }

  private fun findInSeq(id: Long, groupSequence: List<CombineGroup>): CombineGroup? {
    for (group in groupSequence) {
      if (IdCommons.equal(group.origin.id, id)) {
        return group
      }
    }
    return null
  }
}
