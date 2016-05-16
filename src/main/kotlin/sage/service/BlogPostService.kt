package sage.service

import httl.util.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sage.domain.commons.BadArgumentException
import sage.domain.commons.DomainException
import sage.domain.search.SearchBase
import sage.entity.Blog
import sage.entity.Tag
import sage.entity.User
import sage.transfer.BlogView

@Service
class BlogPostService
@Autowired constructor(private val searchBase: SearchBase) {

  fun post(userId: Long, title: String, content: String, tagIds: Collection<Long>): Blog {
    checkLength(title, content)
    val blog = Blog(title, content, User.ref(userId), Tag.multiGet(tagIds))
    blog.escapeAndSetText().save()
    searchBase.index(blog.id, BlogView(blog))
    return blog
  }

  fun edit(userId: Long, blogId: Long, title: String, content: String, tagIds: Collection<Long>): Blog {
    checkLength(title, content)
    val blog = get(blogId)
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
    val blog = get(blogId)
    if (userId != blog.author.id) {
      throw DomainException("User[%d] is not the author of Blog[%d]", userId, blogId)
    }
    blog.delete()
    searchBase.delete(BlogView::class.java, blog.id)
  }

  private fun get(blogId: Long) =
      Blog.byId(blogId) ?: throw DomainException("Blog{%s] does not exist", blogId)

  private fun checkLength(title: String, content: String) {
    if (title.isEmpty() || title.length > BLOG_TITLE_MAX_LEN
        || content.isEmpty() || content.length > BLOG_CONTENT_MAX_LEN) {
      throw BAD_INPUT_LENGTH
    }
  }

  private fun Blog.escapeAndSetText(): Blog {
    title = StringUtils.escapeXml(title)
    content = StringUtils.escapeXml(content).replace("\n", "  \n")
    return this
  }

  companion object {
    private val BLOG_TITLE_MAX_LEN = 100
    private val BLOG_CONTENT_MAX_LEN = 10000
    private val BAD_INPUT_LENGTH = BadArgumentException(
        "输入长度不正确(标题1~100字,内容1~10000字)")
  }
}
