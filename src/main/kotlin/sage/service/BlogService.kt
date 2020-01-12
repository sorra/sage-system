package sage.service

import com.avaje.ebean.Ebean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sage.domain.cache.GlobalCaches
import sage.domain.commons.ContentParser
import sage.domain.commons.ReplaceMention
import sage.domain.intelligence.TagAnalyzer
import sage.domain.permission.BlogPermission
import sage.entity.*
import sage.transfer.BlogPreview
import sage.util.JsoupUtil
import sage.util.MarkdownUtil
import sage.util.StringUtil
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
    val intelliTagIds = TagAnalyzer(title + inputContent, tagIds, userService.topTags(userId).map { it.id }).analyze()

    val blog = Blog(title, inputContent, "", User.ref(userId), Tag.multiGet(intelliTagIds), Blog.contentTypeValue(contentType))
    val mentionedIds = renderAndGetMentions(blog)

    blog.validate()

    Ebean.execute {
      blog.save()
      BlogStat(id = blog.id, whenCreated = blog.whenCreated).save()
      tweetPostService.postForBlog(blog)
    }

    userService.updateUserTag(userId, intelliTagIds)

    mentionedIds.forEach { atId -> notifService.mentionedByBlog(atId, userId, blog.id) }

    index(blog)
    GlobalCaches.blogsCache.clear()
    GlobalCaches.tweetsCache.clear()

    return blog
  }

  fun edit(userId: Long, blogId: Long, title: String, inputContent: String, tagIds: Set<Long>, contentType: String): Blog {
    val blog = Blog.get(blogId)

    BlogPermission(userId, blog).canEdit()

    val oldInputContent = blog.inputContent

    blog.title = title
    blog.inputContent = inputContent
    blog.contentType = Blog.contentTypeValue(contentType)
    val mentionedIds = renderAndGetMentions(blog)
    blog.tags = Tag.multiGet(tagIds)
    blog.whenEdited = Timestamp(System.currentTimeMillis())

    blog.validate()

    blog.update()

    userService.updateUserTag(userId, tagIds)

    if (mentionedIds.isNotEmpty()) {
      val (_, oldMentionedIds) = parseMentions(oldInputContent)
      (mentionedIds - oldMentionedIds).forEach { atId -> notifService.mentionedByBlog(atId, userId, blogId) }
    }

    index(blog)

    return blog
  }

  fun delete(userId: Long, blogId: Long) {
    val blog = Blog.query().setId(blogId).setIncludeSoftDeletes().findUnique()!!

    BlogPermission(userId, blog).canDelete()

    blog.delete()
    blog.stat()?.let {
      it.rank = 0.0
      it.tune = -1
      it.update()
    }

    unindex(blog)
  }

  fun comment(userId: Long, inputContent: String, blogId: Long, replyUserId: Long?): Comment {
    val (hyperContent, mentionedIds) = ContentParser.comment(inputContent) { name -> User.byName(name) }

    val comment = Comment(inputContent, hyperContent, User.ref(userId), Comment.BLOG, blogId, replyUserId)

    comment.validate()

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

  fun search(query: String): List<BlogPreview> {
    val searchHits = searchService.search(SearchService.BLOG, query).hits

    return searchHits.hits.mapNotNull {
      val id = it.id.toLong()
      Blog.byId(id)
    }.map(::BlogPreview)
  }

  fun reindexAll() {
    Blog.where().setIncludeSoftDeletes().findEach {
      if (it.deleted) {
        unindex(it)
      } else {
        index(it)
      }
    }
  }

  private fun index(blog: Blog) {
    searchService.index(SearchService.BLOG, blog.id, blog.toSearchableBlog())
  }

  private fun unindex(blog: Blog) {
    searchService.delete(SearchService.BLOG, blog.id)
  }

  private val maxLatest = 30

  fun hotBlogs() : List<Blog> {
    val stats = BlogStat.where().orderBy("rank desc, id desc").setMaxRows(maxLatest).findList()
    return stats.mapNotNull { Blog.byId(it.id) }
  }

  fun homeRSS(): List<Blog> {
    val blogs = Blog.orderBy("id desc").setMaxRows(maxLatest).findList()
    blogs.forEach {
      it.content = StringUtil.escapeXmlInvalidChar(it.content)
    }
    return blogs
  }

  fun tagRSS(tagId: Long): List<Blog> {
    val blogs = Blog.where().`in`("tags", Tag.ref(tagId)).setMaxRows(maxLatest).findList()
    blogs.forEach {
      it.content = StringUtil.escapeXmlInvalidChar(it.content)
    }
    return blogs
  }

  fun authorRSS(authorId: Long): List<Blog> {
    val blogs = Blog.byAuthor(authorId)
    blogs.forEach {
      it.content = StringUtil.escapeXmlInvalidChar(it.content)
    }
    return blogs
  }

  private fun renderAndGetMentions(blog: Blog) : Set<Long> {
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
