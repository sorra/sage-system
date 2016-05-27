package sage.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sage.domain.commons.BadArgumentException
import sage.domain.commons.DomainException
import sage.domain.commons.Links
import sage.domain.search.SearchBase
import sage.entity.Blog
import sage.entity.Tag
import sage.entity.User
import sage.transfer.BlogView

@Service
class BlogService
@Autowired constructor(private val searchBase: SearchBase) {

  fun post(userId: Long, title: String, content: String, tagIds: Set<Long>): Blog {
    checkLength(title, content)
    val blog = Blog(title, content, User.ref(userId), Tag.multiGet(tagIds))
    blog.escapeAndSetText().save()
    searchBase.index(blog.id, BlogView(blog))
    return blog
  }

  fun edit(userId: Long, blogId: Long, title: String, content: String, tagIds: Set<Long>): Blog {
    checkLength(title, content)
    val blog = Blog.get(blogId)
    if (userId != blog.author.id) {
      throw DomainException("User[%s] is not the author of Blog[%s]", userId, blogId)
    }
    blog.title = title
    blog.content = content
    blog.tags = Tag.multiGet(tagIds)
    blog.escapeAndSetText().update()
    searchBase.index(blog.id, BlogView(blog))
    return blog
  }

  fun delete(userId: Long, blogId: Long) {
    val blog = Blog.get(blogId)
    if (userId != blog.author.id) {
      throw DomainException("User[%d] is not the author of Blog[%d]", userId, blogId)
    }
    blog.delete()
    searchBase.delete(BlogView::class.java, blog.id)
  }

  fun pickedBlogs() = Blog.recent(20)

  private fun checkLength(title: String, content: String) {
    if (title.isEmpty() || title.length > BLOG_TITLE_MAX_LEN
        || content.isEmpty() || content.length > BLOG_CONTENT_MAX_LEN) {
      throw BAD_INPUT_LENGTH
    }
  }

  private fun Blog.escapeAndSetText(): Blog {
    content = content.replace("\n", "  \n") // "  \n" is Markdown paragraph
    content = Links.linksToHtml(content)
    return this
  }

  companion object {
    private val BLOG_TITLE_MAX_LEN = 100
    private val BLOG_CONTENT_MAX_LEN = 10000
    private val BAD_INPUT_LENGTH = BadArgumentException(
        "输入长度不正确(标题1~100字,内容1~10000字)")
  }
}
