package sage.service

import com.avaje.ebean.Ebean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sage.domain.cache.GlobalCaches
import sage.domain.commons.ContentParser
import sage.domain.commons.ReplaceMention
import sage.domain.constraints.Authority
import sage.domain.constraints.BlogConstraints
import sage.domain.constraints.CommentConstraints
import sage.domain.permission.CheckPermission
import sage.entity.*
import sage.transfer.BlogView
import sage.util.JsoupUtil
import sage.util.MarkdownUtil
import java.sql.Timestamp
import java.util.*

@Service
class BlogService
@Autowired constructor(
    private val tweetPostService: TweetPostService,
    private val userService: UserService,
    private val searchService: SearchService,
    private val notifService: NotificationService) {

  fun post(userId: Long, title: String, inputContent: String, tagIds: Set<Long>, contentType: String): Blog {
    val blog = Blog(title, inputContent, "", User.ref(userId), Tag.multiGet(tagIds), Blog.contentTypeValue(contentType))
    val mentionedIds = renderAndGetMentions(blog)

    BlogConstraints.check(blog)

    Ebean.execute {
      blog.save()
      BlogStat(id = blog.id, whenCreated = blog.whenCreated).save()
      tweetPostService.postForBlog(blog)
    }

    userService.updateUserTag(userId, tagIds)

    mentionedIds.forEach { atId -> notifService.mentionedByBlog(atId, userId, blog.id) }

    searchService.index(blog.id, BlogView(blog))
    GlobalCaches.blogsCache.clear()
    GlobalCaches.tweetsCache.clear()
    return blog
  }

  fun edit(userId: Long, blogId: Long, title: String, inputContent: String, tagIds: Set<Long>, contentType: String): Blog {
    val blog = Blog.get(blogId)

    CheckPermission.canEdit(userId, blog, userId == blog.author.id)

    val oldInputContent = blog.inputContent

    blog.title = title
    blog.inputContent = inputContent
    blog.contentType = Blog.contentTypeValue(contentType)
    val mentionedIds = renderAndGetMentions(blog)
    blog.tags = Tag.multiGet(tagIds)
    blog.whenEdited = Timestamp(System.currentTimeMillis())

    BlogConstraints.check(blog)

    blog.update()

    userService.updateUserTag(userId, tagIds)

    if (mentionedIds.isNotEmpty()) {
      val (_, oldMentionedIds) = parseMentions(oldInputContent)
      (mentionedIds - oldMentionedIds).forEach { atId -> notifService.mentionedByBlog(atId, userId, blogId) }
    }

    searchService.index(blog.id, BlogView(blog))
    return blog
  }

  fun delete(userId: Long, blogId: Long) {
    val blog = Blog.query().setId(blogId).setIncludeSoftDeletes().findUnique()!!

    CheckPermission.canDelete(userId, blog, userId == blog.author.id || Authority.isSiteAdmin(User.get(userId).authority))

    blog.delete()
    blog.stat()?.let {
      it.rank = 0.0
      it.tune = -1
      it.update()
    }

    searchService.delete(BlogView::class.java, blog.id)
  }

  fun comment(userId: Long, inputContent: String, blogId: Long, replyUserId: Long?): Comment {
    val (hyperContent, mentionedIds) = ContentParser.comment(inputContent) { name -> User.byName(name) }

    val comment = Comment(inputContent, hyperContent, User.ref(userId), Comment.BLOG, blogId, replyUserId)

    CommentConstraints.check(inputContent)

    comment.save()
    BlogStat.incComments(blogId)
    TweetStat.incComments(Blog.get(blogId).tweetId)
    
    notifService.commented(Blog.get(blogId).author.id, userId, comment.id)
    if (replyUserId != null) {
      notifService.replied(replyUserId, userId, comment.id)
    }
    mentionedIds.forEach { atId -> notifService.mentionedByComment(atId, userId, comment.id) }
    return comment
  }

  fun hotBlogs() : List<Blog> {
    val stats = BlogStat.where().orderBy("rank desc, id desc").setMaxRows(20).findList()
    return stats.mapNotNull { Blog.byId(it.id) }
  }

  fun renderAndGetMentions(blog: Blog) : Set<Long> {
    var content = blog.inputContent

    val pair = parseMentions(content)
    content = pair.first
    val mentionedIds = pair.second

    if (blog.contentType == Blog.MARKDOWN) {
      content = MarkdownUtil.render(content)
      content = JsoupUtil.clean(content)
    } else {
      content = JsoupUtil.clean(content)
    }
    blog.content = content
    return mentionedIds
  }

  private fun parseMentions(text: String): Pair<String, Set<Long>> {
    val mentionedIds = HashSet<Long>()
    return ReplaceMention.with {User.byName(it)}.apply(text, mentionedIds) to mentionedIds
  }
}
