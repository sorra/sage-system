package sage.entity

import com.avaje.ebean.annotation.SoftDelete
import sage.domain.constraints.BlogConstraints
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity
class Blog(
    var title: String,

    @Column(columnDefinition = "TEXT")
    @Lob
    var inputContent: String,

    hyperContent: String,

    @ManyToOne(optional = false)
    val author: User, tags: Set<Tag>,

    var contentType: Short
) : BaseModel() {

  @Column(columnDefinition = "TEXT")
  @Lob
  var content: String = hyperContent

  @ManyToMany(cascade = arrayOf(CascadeType.ALL))
  var tags: MutableSet<Tag> = HashSet()

  var whenEdited: Timestamp? = null

  @Column(nullable = false)
  var tweetId: Long = 0

  @SoftDelete
  var deleted: Boolean = false

  fun stat() = BlogStat.byId(id)

  fun validate() {
    BlogConstraints.check(this)
  }

  companion object : Find<Long, Blog>() {
    val MARKDOWN: Short = 1
    val RICHTEXT: Short = 2
    fun contentTypeValue(contentType: String): Short = when(contentType.toLowerCase()) {
      "markdown" -> MARKDOWN
      "richtext" -> RICHTEXT
      else -> 0
    }
    fun contentTypeString(contentType: Short): String = when(contentType) {
      MARKDOWN -> "markdown"
      RICHTEXT -> "richtext"
      else -> ""
    }

    fun get(id: Long) = getNonNull(Blog::class, id)

    fun byAuthor(authorId: Long): List<Blog> = where().eq("author", User.ref(authorId)).orderBy("id desc").findList()
  }

  init {
    this.tags = HashSet(tags)
  }
}