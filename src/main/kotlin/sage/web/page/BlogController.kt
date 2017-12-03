package sage.web.page

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.ModelAndView
import sage.domain.cache.GlobalCaches
import sage.domain.permission.BlogPermission
import sage.entity.*
import sage.transfer.BlogPreview
import sage.transfer.BlogView
import sage.web.auth.Auth
import sage.web.context.BaseController

@Controller
@RequestMapping("/blogs")
class BlogController : BaseController() {
  @RequestMapping("/new", method = arrayOf(GET))
  fun newPage(@RequestParam(required = false) contentType: String?): ModelAndView {
    val uid = Auth.checkUid()
    val topTags = userService.topTags(uid)
    return ModelAndView("write-blog")
        .addObject("contentType", contentType)
        .addObject("topTags", topTags)
  }

  @RequestMapping("/new", method = arrayOf(POST))
  @ResponseBody
  fun create(@RequestParam title: String, @RequestParam content: String,
                  @RequestParam contentType: String,
                  @RequestParam(required = false) draftId: Long?): String {
    val uid = Auth.checkUid()
    val tagIds = tagIds()
    val blog = blogService.post(uid, title, content, tagIds, contentType).run { BlogView(this) }
    draftId?.let { Draft.deleteById(it) }
    return "/blogs/${blog.id}"
  }

  @RequestMapping("/{id}/edit", method = arrayOf(GET))
  fun editPage(@PathVariable id: Long, @RequestParam(required = false) contentType: String?): ModelAndView {
    val uid = Auth.checkUid()
    val blog = Blog.get(id)
    BlogPermission(uid, blog).canEdit()

    val blogView = blog.let { BlogView(it, showInputContent = true) }
    val topTags = userService.filterNewTags(uid, blogView.tags)
    return ModelAndView("write-blog")
        .addObject("blog", blogView).addObject("contentType", contentType)
        .addObject("existingTags", blogView.tags).addObject("topTags", topTags)
  }

  @RequestMapping("/{id}/edit", method = arrayOf(POST))
  @ResponseBody
  fun edit(@PathVariable id: Long, @RequestParam title: String, @RequestParam content: String,
                @RequestParam contentType: String,
                @RequestParam(required = false) draftId: Long?): String {
    val uid = Auth.checkUid()
    val tagIds = tagIds()
    blogService.edit(uid, id, title, content, tagIds, contentType)
    draftId?.let { Draft.deleteById(it) }
    return "/blogs/$id"
  }

  @RequestMapping("/{id}")
  fun get(@PathVariable id: Long) : ModelAndView {
    val blog = Blog.get(id).run { BlogView(this) }
    blog.views += 1
    BlogStat.incViews(id)
    val isLiked: Boolean? = Auth.uid()?.let { Liking.find(it, Liking.BLOG, id) != null }
    return ModelAndView("blog").addObject("blog", blog).addObject("isLiked", isLiked)
  }

  @RequestMapping("/{id}/delete", method = arrayOf(POST))
  fun delete(@PathVariable id: Long): String {
    val uid = Auth.checkUid()
    blogService.delete(uid, id)
    return "redirect:/"
  }

  @RequestMapping
  fun all(): ModelAndView {
    val pageIndex = pageIndex()
    val pageSize = pageSize()

    val (blogs, pagesCount) = GlobalCaches.blogsCache["/blogs", pageIndex, pageSize]

    return pagedModelAndView("blogs", blogs.map(::BlogPreview), pagesCount, pageIndex)
  }
}