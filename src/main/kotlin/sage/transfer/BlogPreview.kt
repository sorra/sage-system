package sage.transfer

import sage.annotation.KotlinNoArg
import sage.entity.Blog
import sage.util.Strings
import java.sql.Timestamp

@KotlinNoArg
class BlogPreview {
  var id: Long = 0
  var author: UserLabel? = null
  var title: String = ""
  var summary: String = ""
  var whenCreated: Timestamp? = null
  var whenModified: Timestamp? = null
  var tags: List<TagLabel> = arrayListOf()
  var tweetId: Long = 0
  var commentCount: Int = 0
  var likes: Int = 0

  constructor(blog: Blog) {
    id = blog.id
    author = UserLabel(blog.author)

    title = blog.title
    summary = Strings.omit(blog.content, 103)
    whenCreated = blog.whenCreated
    whenModified = actualWhenModified(blog.whenCreated, blog.whenModified)

    tags = blog.tags.map(::TagLabel)
    tweetId = blog.tweetId
    blog.stat()?.let {
      commentCount = it.comments
      likes = it.likes
    }
  }
}
