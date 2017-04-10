package sage.entity

import org.slf4j.LoggerFactory
import sage.domain.commons.Edge
import sage.domain.commons.RichElement
import sage.domain.constraints.TweetConstraints
import sage.transfer.MidForward
import sage.transfer.MidForwards
import sage.web.context.Json
import java.util.*
import javax.persistence.*

@Entity
class Tweet : BaseModel {

  @Column(columnDefinition = "TEXT")
  @Lob
  val inputContent: String
    get() = if (deleted) "" else field

  @Column(columnDefinition = "TEXT")
  @Lob @Basic
  val content: String
    get() = if (deleted) "" else field

  @Column(columnDefinition = "TEXT")
  @Lob @Basic
  var richElementsJson: String?
    get() = if (deleted) null else field

  @Column(columnDefinition = "TEXT")
  @Lob @Basic
  var midForwardsJson: String? = null
    get() = if (deleted) null else field

  @ManyToOne(optional = false)
  val author: User

  @Column(nullable = false)
  var originId: Long = 0

  @Column(nullable = false)
  var blogId: Long = 0

  @ManyToMany(cascade = arrayOf(CascadeType.ALL))
  var tags: MutableSet<Tag> = HashSet()

  var deleted: Boolean = false

  constructor(inputContent: String, hyperContent: String, richElements: Collection<RichElement>, author: User, tags: Set<Tag>) {
    this.inputContent = inputContent
    this.content = hyperContent
    this.author = author
    this.richElementsJson = Json.json(richElements)
    this.tags = HashSet(tags)
  }

  constructor(inputContent: String, hyperContent: String, richElements: Collection<RichElement>, author: User, initialOrigin: Tweet)
      : this(inputContent, hyperContent, richElements, author, initialOrigin.tags) {
    originId = initialOrigin.id
    if (initialOrigin.hasOrigin()) {
      throw IllegalArgumentException(String.format(
          "tweet's origin should not be nested! initialOrigin[%s] and its origin[%s]",
          initialOrigin.id, initialOrigin.originId))
    }
  }

  constructor(inputContent: String, hyperContent: String, richElements: Collection<RichElement>, midForwards: MidForwards, author: User, origin: Tweet)
      : this(inputContent, hyperContent, richElements, author, origin) {
    midForwardsJson = midForwards.toJson()
  }

  constructor(inputContent: String, hyperContent: String, richElements: Collection<RichElement>, author: User, blog: Blog)
      : this(inputContent, hyperContent, richElements, author, blog.tags) {
    blogId = blog.id
  }

  fun hasOrigin() = originId > 0

  fun hasBlog() = blogId > 0

  fun richElements(): Collection<RichElement> =
      try {
        if (richElementsJson != null && richElementsJson != "[]") RichElement.fromJsonToList(richElementsJson!!)
        else emptyList()
      } catch (e: Exception) {
        log.error("richElements cannot be deserialized from JSON", e)
        emptyList<RichElement>()
      }

  fun midForwards(): MidForwards? =
    try {
      midForwardsJson?.run { MidForwards.fromJson(this) }
    } catch (e: Exception) {
      log.error("midForwards cannot be deserialized from JSON", e)
      MidForwards().apply { xs.add(MidForward(0, 0, "", "")) }
    }

  fun stat() = TweetStat.byId(id)

  fun validate() {
    TweetConstraints.check(this)
  }

  companion object : Find<Long, Tweet>() {
    private val log = LoggerFactory.getLogger(Tweet::class.java)

    fun get(id: Long): Tweet = getNonNull(Tweet::class, id)

    fun recent(size: Int): List<Tweet> = default().orderBy("id desc").setMaxRows(size).findList()

    fun byTags(tags: Collection<Tag>, edge: Edge): List<Tweet> {
      if (tags.isEmpty()) {
        return LinkedList()
      }
      return ranged(edge).`in`("tags.id", Tag.getQueryTags(tags).map(Tag::id)).findList()
    }

    fun byAuthor(authorId: Long, edge: Edge = Edge.none()): MutableList<Tweet> = ranged(edge).eq("author", User.ref(authorId)).findList()

    fun byAuthorAndTags(authorId: Long, tags: Collection<Tag>, edge: Edge = Edge.none()): List<Tweet> {
      if (tags.isEmpty()) {
        return LinkedList()
      }
      if (hasRoot(tags)) {
        return byAuthor(authorId, edge)
      }
      return ranged(edge).eq("author", User.ref(authorId)).`in`("tags.id", Tag.getQueryTags(tags).map(Tag::id)).findList()
    }

    fun forwards(originId: Long): List<Tweet> = default().eq("originId", originId).findList()

    fun getOrigin(tweet: Tweet) = if (tweet.hasOrigin()) byId(tweet.originId) else null

    private fun ranged(edge: Edge) = default().apply {
      when (edge.type) {
        Edge.EdgeType.NONE -> {}
        Edge.EdgeType.BEFORE -> lt("id", edge.edgeId)
        Edge.EdgeType.AFTER -> gt("id", edge.edgeId)
        else -> throw UnsupportedOperationException()
      }
      orderBy("id desc").maxRows = edge.limitCount
    }

    private fun default() = where().eq("deleted", false)

    private fun hasRoot(tags: Collection<Tag>) = tags.find { it.id == Tag.ROOT_ID } != null
  }
}