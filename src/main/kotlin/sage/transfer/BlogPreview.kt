package sage.transfer

import sage.entity.Blog
import sage.util.Strings
import java.sql.Timestamp

class BlogPreview {
  var id: Long = 0
  var author: UserLabel? = null
  var title: String = ""
  var summary: String = ""
  var createdTime: Timestamp? = null
  var modifiedTime: Timestamp? = null
  var tags: List<TagLabel> = arrayListOf()

  internal constructor() {
  }

  constructor(blog: Blog) {
    id = blog.id
    author = UserLabel(blog.author)

    title = blog.title
    summary = Strings.omit(blog.content, 200)
    createdTime = blog.whenCreated
    modifiedTime = actualWhenModified(blog.whenCreated, blog.whenModified)

    tags = blog.tags.map { TagLabel(it) }
  }
}
