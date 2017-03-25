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
  var whenEdited: Timestamp? = null
  var tags: List<TagLabel> = arrayListOf()
  var tweetId: Long = 0
  var commentCount: Int = 0
  var likes: Int = 0

  constructor(blog: Blog) {
    id = blog.id
    author = blog.author.toUserLabel()

    title = blog.title
    if (blog.contentType == Blog.MARKDOWN) {
      summary = Strings.escapeHtmlTag(Strings.omit(blog.inputContent, 103))
    } else {
      summary = Strings.escapeHtmlTag(Strings.omit(blog.content, 103))
    }
    whenCreated = blog.whenCreated
    whenEdited = actualWhenEdited(blog.whenCreated, blog.whenEdited)

    tags = blog.tags.map { it.toTagLabel() }
    tweetId = blog.tweetId
    blog.stat()?.let {
      commentCount = it.comments
      likes = it.likes
    }
  }
}
