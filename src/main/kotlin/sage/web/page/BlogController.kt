package sage.web.page

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.ModelAndView
import sage.domain.cache.GlobalCaches
import sage.entity.*
import sage.service.BlogService
import sage.service.UserService
import sage.transfer.BlogPreview
import sage.transfer.BlogView
import sage.transfer.CommentView
import sage.util.PaginationLogic
import sage.web.auth.Auth
import sage.web.context.RenderUtil

@Controller
@RequestMapping("/blogs")
open class BlogController @Autowired constructor(
    private val blogService: BlogService,
    private val userService: UserService) {
  @RequestMapping("/new", method = arrayOf(GET))
  open fun newPage(): ModelAndView {
    val uid = Auth.checkUid()
    val topTags = userService.topTags(uid)
    return ModelAndView("write-blog").addObject("topTags", topTags)
  }

  @RequestMapping("/new", method = arrayOf(POST))
  @ResponseBody
  open fun create(@RequestParam title: String, @RequestParam content: String,
                  @RequestParam("tagIds[]", defaultValue = "") tagIds: Set<Long>): String {
    val uid = Auth.checkUid()
    val blog = blogService.post(uid, title, content, tagIds).run { BlogView(this) }
    return "/blogs/${blog.id}"
  }

  @RequestMapping("/{id}/edit", method = arrayOf(GET))
  open fun editPage(@PathVariable id: Long): ModelAndView {
    val uid = Auth.checkUid()
    val blog = Blog.get(id).run { BlogView(this) }
    val topTags = userService.filterNewTags(uid, blog.tags)
    return ModelAndView("write-blog")
        .addObject("blog", blog)
        .addObject("existingTags", blog.tags).addObject("topTags", topTags)
  }

  @RequestMapping("/{id}/edit", method = arrayOf(POST))
  @ResponseBody
  open fun edit(@PathVariable id: Long, @RequestParam title: String, @RequestParam content: String,
                @RequestParam("tagIds[]", defaultValue = "") tagIds: Set<Long>): String {
    val uid = Auth.checkUid()
    blogService.edit(uid, id, title, content, tagIds)
    return "/blogs/$id"
  }

  @RequestMapping("/{id}")
  open fun get(@PathVariable id: Long) : ModelAndView {
    val blog = Blog.get(id).run { BlogView(this) }
    blog.views += 1
    BlogStat.incViews(id)
    val isLiked: Boolean? = Auth.uid()?.run { Liking.find(this, Liking.BLOG, id) != null }
    return ModelAndView("blog").addObject("blog", blog).addObject("isLiked", isLiked)
  }

  @RequestMapping("/{id}/delete", method = arrayOf(POST))
  open fun delete(@PathVariable id: Long): String {
    val uid = Auth.checkUid()
    blogService.delete(uid, id)
    return "redirect:/"
  }

  @RequestMapping("/{id}/like")
  @ResponseBody
  open fun like(@PathVariable id: Long) {
    val uid = Auth.checkUid()
    BlogStat.like(id, uid)
  }

  @RequestMapping("/{id}/unlike")
  @ResponseBody
  open fun unlike(@PathVariable id: Long) {
    val uid = Auth.checkUid()
    BlogStat.unlike(id, uid)
  }

  @RequestMapping("/{id}/likes")
  @ResponseBody
  open fun likes(@PathVariable id: Long) = BlogStat.get(id).likes

  @RequestMapping
  open fun all(@RequestParam(defaultValue = "1") page: Int): ModelAndView {
    val size = 20
    val (blogs, pagesCount) = GlobalCaches.blogsCache["/blogs?page=$page&size=$size", {
      val blogs = Blog.orderBy("id desc").findPagedList(page-1, size).list.map(::BlogPreview)
      val pagesCount: Int = PaginationLogic.pagesCount(size, getRecordsCount(Blog))
      blogs to pagesCount
    }] as Pair<*, *>
    return ModelAndView("blogs").addObject("blogs", blogs)
        .addObject("paginationLinks", RenderUtil.paginationLinks("/blogs", pagesCount as Int, page))
  }
}