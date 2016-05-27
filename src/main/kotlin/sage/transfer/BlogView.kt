package sage.transfer

import sage.entity.Blog
import java.sql.Timestamp

class BlogView {
  var id: Long = 0
  var author: UserLabel? = null
  var title: String = ""
  var content: String = ""
  var createdTime: Timestamp? = null
  var modifiedTime: Timestamp? = null
  var tags: List<TagLabel> = arrayListOf()

  internal constructor() {
  }

  constructor(blog: Blog) {
    id = blog.id
    author = UserLabel(blog.author)

    title = blog.title
    content = blog.content
    createdTime = blog.whenCreated
    modifiedTime = actualWhenModified(blog.whenCreated, blog.whenModified)

    tags = blog.tags.map { TagLabel(it) }
  }
}
