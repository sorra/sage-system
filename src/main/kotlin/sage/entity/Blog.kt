package sage.entity

import com.avaje.ebean.annotation.SoftDelete
import java.util.*
import javax.persistence.*

@Entity
class Blog : BaseModel {

  var title: String = ""

  @Column(columnDefinition = "TEXT") @Lob
  var content: String = ""

  @ManyToOne(optional = false)
  var author: User

  @ManyToMany(fetch = FetchType.EAGER)
  var tags: MutableSet<Tag> = HashSet()

  @SoftDelete
  var deleted: Boolean = false

  constructor(title: String, content: String, author: User, tags: Set<Tag>) {
    this.title = title
    this.content = content
    this.author = author
    this.tags = HashSet(tags)
  }

  companion object : Find<Long, Blog>() {
    fun get(id: Long) = getNonNull(Blog::class, id)

    fun byAuthor(authorId: Long) = where().eq("author", User.ref(authorId)).findList()
  }
}