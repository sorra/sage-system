package sage.transfer

import sage.annotation.KotlinNoArg
import sage.domain.commons.IdCommons
import sage.entity.Blog
import sage.entity.Tag
import sage.entity.Tweet
import sage.web.render.RenderUtil
import java.util.*

@KotlinNoArg
class TweetView {
  var id: Long = 0
  var authorId: Long = 0
  var authorName: String = ""
  var avatar: String = ""
  var content: String = ""
  var time: Date? = null

  var origin: TweetView? = null

  var midForwards: MidForwards? = null
  var tags: Collection<TagLabel> = mutableListOf()

  var forwardCount: Int = 0
  var commentCount: Int = 0
  var likes: Int = 0

  var isLiked: Boolean = false

  var from: String = ""
  var fromTagId: Long = 0

  constructor(tweet: Tweet, origin: Tweet?, showsStat: Boolean, isLikedChecker: (Long) -> Boolean) {
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
      this.origin = TweetView(origin, null, showsStat, isLikedChecker)
    }
    midForwards = tweet.midForwards()

    tags = tweet.tags.map(Tag::toTagLabel)

    tweet.stat()?.let {
      this.forwardCount = it.forwards
      this.commentCount = it.comments
      this.likes = it.likes
    }

    isLiked = isLikedChecker(id)
  }

  fun beFromTag(tagId: Long): TweetView {
    fromTagId = tagId
    return this
  }

  /**
   * used by StreamService#combineToGroups()
   */
  fun clearOrigin() {
    origin = null
  }


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
    return "id=$id $authorName: $content$tags"
  }

  private fun convertRichElements(tweet: Tweet): String {
    val sb = StringBuilder(tweet.content)
    tweet.richElements().forEach { (type, value) ->
      if (type == "picture") {
        val link = RenderUtil.cdn() + value
        sb.append(" <a class=\"img-origin-link\" href=\"$link\" target=\"_blank\">查看原图</a>")
        sb.append("<img class=\"view-img\" src=\"$link\"/>")
      }
    }
    return sb.toString()
  }
}
