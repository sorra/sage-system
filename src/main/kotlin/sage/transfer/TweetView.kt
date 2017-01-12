package sage.transfer

import sage.annotation.KotlinNoArg
import sage.domain.commons.IdCommons
import sage.entity.Blog
import sage.entity.Tweet
import sage.entity.TweetStat
import java.util.*

@KotlinNoArg
class TweetView : Item {
  private val type = "TweetView"

  var id: Long = 0
  var authorId: Long = 0
  var authorName: String = ""
  var avatar: String = ""
  var content: String = ""
  var time: Date? = null
  private var origin: TweetView? = null
  var midForwards: MidForwards? = null
  private val tags = ArrayList<TagLabel>()

  var forwardCount: Int = 0
  var commentCount: Int = 0
  var likes: Int = 0

  var isLiked: Boolean = false

  var fromTag: Long? = null

  constructor(tweet: Tweet, origin: Tweet?, isLikedChecker: (Long) -> Boolean, tweetStatFinder: (Long) -> TweetStat? = {null}) {
    id = tweet.id
    if (!tweet.deleted) {
      authorId = tweet.author.id
      authorName = tweet.author.name
      avatar = tweet.author.avatar
    }
    content = convertRichElements(tweet)
    if (tweet.blogId > 0) {
      Blog.byId(tweet.blogId)?.let {
        val titleLink = "<a class=\"tweet-blog-title\" href=\"/blogs/${it.id}\">${it.title}</a>"
        content = "发表了[$titleLink] $content"
      }
    }
    time = tweet.whenCreated
    if (origin != null) {
      this.origin = TweetView(origin, null, isLikedChecker, tweetStatFinder)
    }
    midForwards = tweet.midForwards()
    for (tag in tweet.tags) {
      tags.add(TagLabel(tag))
    }

    val tweetStat = tweetStatFinder(id)
    if (tweetStat != null) {
      this.forwardCount = tweetStat.forwards
      this.commentCount = tweetStat.comments
      this.likes = tweetStat.likes
    }

    isLiked = isLikedChecker(id)
  }

  fun beFromTag(tagId: Long?): TweetView {
    fromTag = tagId
    return this
  }

  /**
   * used by CombineGroup
   */
  fun clearOrigin() {
    origin = null
  }

  override fun getType(): String = type

  override fun getTags(): List<TagLabel> = tags

  override fun getOrigin(): TweetView? = origin

  override fun hashCode(): Int {
    return IdCommons.hashCode(id)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other)
      return true
    if (other == null)
      return false
    if (javaClass != other.javaClass)
      return false
    return IdCommons.equal(id, (other as TweetView).id)
  }

  override fun toString(): String {
    return authorName + ": " + content + tags
  }

  private fun convertRichElements(tweet: Tweet): String {
    val sb = StringBuilder(tweet.content)
    tweet.richElements().forEach { elem ->
      if (elem.type == "picture") {
        sb.append("<img class=\"view-img\" src=\"").append(elem.value).append("\"/>")
      }
    }
    return sb.toString()
  }
}
