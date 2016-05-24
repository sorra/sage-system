package sage.entity

import org.slf4j.LoggerFactory
import sage.domain.commons.Edge
import sage.transfer.MidForward
import sage.transfer.MidForwards
import java.util.*
import javax.persistence.*

@Entity
class Tweet : BaseModel {

  @Column(columnDefinition = "TEXT", length = 65535)
  @Lob @Basic
  var content: String? = null
    get() = if (deleted) "" else field

  @ManyToOne(optional = false)
  var author: User

  var originId: Long = 0
    @Column(nullable = false)
    get() = if (deleted) -1 else field

  @Column(columnDefinition = "TEXT", length = 65535)
  @Lob @Basic
  var midForwardsJson: String? = null
    get() = if (deleted) null else field

  var blogId: Long = 0

  @ManyToMany
  var tags: MutableSet<Tag> = HashSet()

  var deleted: Boolean = false

  constructor(content: String, author: User, tags: Set<Tag>) {
    this.content = content
    this.author = author
    this.tags = HashSet(tags)
  }

  constructor(content: String, author: User, initialOrigin: Tweet) : this(content, author, initialOrigin.tags) {
    originId = initialOrigin.id
    if (initialOrigin.hasOrigin()) {
      throw IllegalArgumentException(String.format(
          "tweet's origin should not be nested! initialOrigin[%s] and its origin[%s]",
          initialOrigin.id, initialOrigin.originId))
    }
  }

  constructor(content: String, author: User, origin: Tweet, midForwards: MidForwards) : this(content, author, origin) {
    midForwardsJson = midForwards.toJson()
  }

  constructor(content: String, author: User, sourceBlog: Blog) : this(content, author, sourceBlog.tags) {
    blogId = sourceBlog.id
  }

  fun hasOrigin() = originId > 0

  fun midForwards(): MidForwards? = try {
    midForwardsJson?.run { MidForwards.fromJson(this) }
  } catch (e: Exception) {
    log.error("midForwards cannot be deserialized from JSON", e)
    MidForwards().apply { xs.add(MidForward(0, "//?")) }
  }

  @Suppress("NAME_SHADOWING")
  companion object : Find<Long, Tweet>() {
    private val log = LoggerFactory.getLogger(Tweet::class.java)

    fun byTags(tags: Collection<Tag>, edge: Edge): List<Tweet> {
      if (tags.isEmpty()) {
        return LinkedList()
      }
      val tags = Tag.getQueryTags(tags)
      return ranged(edge).filterMany("tags").`in`("id", tags.map { it.id }).findList()
    }

    fun byAuthor(authorId: Long, edge: Edge = Edge.none()) =
        ranged(edge).eq("author", User.ref(authorId)).findList()

    fun byAuthorAndTags(authorId: Long, tags: Collection<Tag>, edge: Edge = Edge.none()): List<Tweet> {
      if (tags.isEmpty()) {
        return LinkedList()
      }
      if (hasRoot(tags)) {
        return byAuthor(authorId, edge)
      }
      val tags = Tag.getQueryTags(tags)
      return ranged(edge).eq("author", User.ref(authorId))
          .filterMany("tags").`in`("id", tags.map { it.id }).findList()
    }

    fun connectTweets(blogId: Long): List<Tweet> {
      val shares = where().eq("blogId", blogId).findList()
      val connected = ArrayList(shares)
      if (!shares.isEmpty()) {
        val reshares = where().`in`("originId", shares.map { it.id }.toHashSet()).findList()
        connected.addAll(reshares)
      }
      return connected
    }

    fun forwards(originId: Long) = where().eq("originId", originId).findList()
    fun forwardCount(originId: Long) = where().eq("originId", originId).findRowCount()

    fun getOrigin(tweet: Tweet) = if (tweet.hasOrigin()) byId(tweet.originId) else null

    private fun ranged(edge: Edge) = where().apply {
      when (edge.type) {
        Edge.EdgeType.NONE -> {}
        Edge.EdgeType.BEFORE -> lt("id", edge.edgeId)
        Edge.EdgeType.AFTER -> gt("id", edge.edgeId)
        else -> throw UnsupportedOperationException()
      }
      eq("deleted", false).orderBy("id desc").maxRows = Edge.FETCH_SIZE
    }

    private fun hasRoot(tags: Collection<Tag>) = tags.find { it.id == Tag.ROOT_ID } != null
  }
}