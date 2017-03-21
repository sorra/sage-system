package sage.entity

import com.avaje.ebean.annotation.SoftDelete
import java.util.*
import javax.persistence.*

@Entity
class Blog : BaseModel {

  var title: String

  @Column(columnDefinition = "TEXT")
  @Lob
  var inputContent: String

  @Column(columnDefinition = "TEXT")
  @Lob @Basic
  var content: String

  @ManyToOne(optional = false)
  val author: User

  @ManyToMany(cascade = arrayOf(CascadeType.ALL))
  var tags: MutableSet<Tag> = HashSet()

  var contentType: Short

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

    fun get(id: Long) = getNonNull(Blog::class, id)

    fun byAuthor(authorId: Long): List<Blog> = where().eq("author", User.ref(authorId)).orderBy("id desc").findList()
  }
}