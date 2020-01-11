package sage.entity

import com.avaje.ebean.annotation.SoftDelete
import sage.domain.constraints.BlogConstraints
import sage.transfer.SearchableBlog
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity
class Blog(
    var title: String,

    @Column(columnDefinition = "TEXT") @Basic(fetch = FetchType.LAZY)
    var inputContent: String,

    hyperContent: String,

    @ManyToOne(optional = false)
    val author: User,

    tags: Set<Tag>,

    var contentType: Short
) : AutoModel() {

  @Column(columnDefinition = "TEXT") @Basic(fetch = FetchType.LAZY)
  var content: String = hyperContent

  @ManyToMany
  var tags: MutableSet<Tag> = HashSet(tags)

  var whenEdited: Timestamp? = null

  @Column(nullable = false)
  var tweetId: Long = 0

  @SoftDelete
  var deleted: Boolean = false

  fun stat() = BlogStat.byId(id)

  fun validate() {
    BlogConstraints.check(this)
  }

  fun toSearchableBlog(): SearchableBlog = SearchableBlog(
      id, author.toUserLabel(), title, content, whenCreated, whenEdited, tags.map(Tag::toTagLabel)
  )

  companion object : BaseFind<Long, Blog>(Blog::class) {
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

    fun byAuthor(authorId: Long): List<Blog> = where().eq("author", User.ref(authorId)).orderBy("id desc").findList()
  }
}