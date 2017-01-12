package sage.transfer

import sage.annotation.KotlinNoArg
import sage.entity.Blog
import java.sql.Timestamp

@KotlinNoArg
class BlogView {
  var id: Long = 0
  var author: UserLabel? = null
  var title: String = ""
  var content: String = ""
  var whenCreated: Timestamp? = null
  var whenModified: Timestamp? = null
  var tags: List<TagLabel> = arrayListOf()

  var likes: Int = 0
  var views: Int = 0

  constructor()

  constructor(blog: Blog) {
    id = blog.id
    author = UserLabel(blog.author)

    title = blog.title
    content = blog.content
    whenCreated = blog.whenCreated
    whenModified = actualWhenModified(blog.whenCreated, blog.whenModified)

    tags = blog.tags.map(::TagLabel)

    blog.stat()?.let {
      likes = it.likes
      views = it.views
    }
  }
}
