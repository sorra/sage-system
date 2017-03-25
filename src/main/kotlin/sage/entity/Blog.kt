package sage.entity

import com.avaje.ebean.annotation.SoftDelete
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity
class Blog : BaseModel {

  var title: String

  @Column(columnDefinition = "TEXT")
  @Lob
  var inputContent: String

  @Column(columnDefinition = "TEXT")
  @Lob
  var content: String

  @ManyToOne(optional = false)
  val author: User

  @ManyToMany(cascade = arrayOf(CascadeType.ALL))
  var tags: MutableSet<Tag> = HashSet()

  var contentType: Short

  var whenEdited: Timestamp? = null

  @Column(nullable = false)
  var tweetId: Long = 0

  @SoftDelete
  var deleted: Boolean = false

  constructor(title: String, inputContent: String, hyperContent: String, author: User, tags: Set<Tag>, contentType: Short) {
    this.title = title
    this.inputContent = inputContent
    this.content = hyperContent
    this.author = author
    this.tags = HashSet(tags)
    this.contentType = contentType
  }

  fun stat() = BlogStat.byId(id)

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
}