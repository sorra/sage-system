package sage.transfer

import sage.annotation.KotlinNoArg
import sage.entity.Blog
import java.sql.Timestamp

@KotlinNoArg
class BlogView {
  var id: Long = 0
  var author: UserLabel? = null
  var title: String = ""
  var contentType: String = ""
  var inputContent: String = ""
  var content: String = ""
  var whenCreated: Timestamp? = null
  var whenEdited: Timestamp? = null
  var tags: List<TagLabel> = arrayListOf()
  var tweetId: Long = 0

  var likes: Int = 0
  var views: Int = 0

  constructor()

  constructor(blog: Blog) : this(blog, false)

  constructor(blog: Blog, showInputContent: Boolean) {
    id = blog.id
    author = blog.author.toUserLabel()

    title = blog.title
    contentType = Blog.contentTypeString(blog.contentType)
    if (showInputContent) inputContent = blog.inputContent
    content = blog.content
    whenCreated = blog.whenCreated
    whenEdited = actualWhenEdited(blog.whenCreated, blog.whenEdited)

    tags = blog.tags.map { it.toTagLabel() }
    tweetId = blog.tweetId

    blog.stat()?.let {
      likes = it.likes
      views = it.views
    }
  }
}
