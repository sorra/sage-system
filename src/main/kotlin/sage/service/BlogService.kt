package sage.service

import com.avaje.ebean.Ebean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sage.domain.cache.GlobalCaches
import sage.domain.commons.*
import sage.entity.*
import sage.transfer.BlogPreview
import sage.transfer.BlogView
import sage.util.Strings
import java.util.*

@Service
@Suppress("NAME_SHADOWING")
class BlogService
@Autowired constructor(
    private val tweetPostService: TweetPostService,
    private val userService: UserService,
    private val searchService: SearchService,
    private val notifService: NotificationService) {

  fun post(userId: Long, title: String, content: String, tagIds: Set<Long>): Blog {
    checkLength(title, content)
    val (content, mentionedIds) = processMarkdownContent(content)

    val blog = Blog(title, content, User.ref(userId), Tag.multiGet(tagIds))
    Ebean.execute {
      blog.save()
      BlogStat(id = blog.id, whenCreated = blog.whenCreated).save()
      tweetPostService.postForBlog(blog)
    }

    userService.updateUserTag(userId, tagIds)

    mentionedIds.forEach { atId -> notifService.mentionedByBlog(atId, userId, blog.id) }

    searchService.index(blog.id, BlogView(blog))
    GlobalCaches.blogsCache.clear()
    return blog
  }

  fun edit(userId: Long, blogId: Long, title: String, content: String, tagIds: Set<Long>): Blog {
    checkLength(title, content)
    val blog = Blog.get(blogId)
    if (userId != blog.author.id) {
      throw DomainException("User[%s] is not the author of Blog[%s]", userId, blogId)
    }
    val (content, mentionedIds) = processMarkdownContent(content)

    blog.title = title
    blog.content = content
    blog.tags = Tag.multiGet(tagIds)
    blog.update()

    userService.updateUserTag(userId, tagIds)

    if (mentionedIds.isNotEmpty()) {
      val (oldContent, oldMentionedIds) = processMarkdownContent(blog.content)
      (mentionedIds - oldMentionedIds).forEach { atId -> notifService.mentionedByBlog(atId, userId, blogId) }
    }

    searchService.index(blog.id, BlogView(blog))
    return blog
  }

  fun delete(userId: Long, blogId: Long) {
    val blog = Blog.get(blogId)
    if (userId != blog.author.id) {
      throw DomainException("User[%d] is not the author of Blog[%d]", userId, blogId)
    }
    blog.delete()

    searchService.delete(BlogView::class.java, blog.id)
  }

  fun comment(userId: Long, content: String, blogId: Long, replyUserId: Long?): Comment {
    if (content.isEmpty() || content.length > COMMENT_MAX_LEN) {
      throw BAD_COMMENT_LENGTH
    }
    val (hyperContent, mentionedIds) = ContentParser.comment(content) { name -> User.byName(name) }

    val comment = Comment(hyperContent, User.ref(userId), Comment.BLOG, blogId, replyUserId)
    comment.save()
    BlogStat.incComments(blogId)
    
    notifService.commented(Blog.get(blogId).author.id, userId, comment.id)
    if (replyUserId != null) {
      notifService.replied(replyUserId, userId, comment.id)
    }
    mentionedIds.forEach { atId -> notifService.mentionedByComment(atId, userId, comment.id) }
    return comment
  }

  fun hotBlogs() : List<Blog> {
    val stats = BlogStat.where().orderBy("rank desc, id desc").setMaxRows(20).findList()
    return stats.map { Blog.get(it.id) }
  }

  private fun checkLength(title: String, content: String) {
    if (title.isEmpty() || title.length > BLOG_TITLE_MAX_LEN
        || content.isEmpty() || content.length > BLOG_CONTENT_MAX_LEN) {
      throw BAD_INPUT_LENGTH
    }
  }

  private fun processMarkdownContent(content: String): Pair<String, HashSet<Long>> {
    var content = Strings.escapeHtmlTag(content)
    val mentionedIds = HashSet<Long>()
    content = ReplaceMention.with {User.byName(it)}.apply(content, mentionedIds)
    return Pair(content, mentionedIds)
  }

  companion object {
    private val BLOG_TITLE_MAX_LEN = 100
    private val BLOG_CONTENT_MAX_LEN = 30000
    private val BAD_INPUT_LENGTH = BadArgumentException(
        "输入长度不正确(标题1~100字,内容1~30000字)")
    private val COMMENT_MAX_LEN = 1000
    private val BAD_COMMENT_LENGTH = BadArgumentException("评论应为1~1000字")
  }
}
