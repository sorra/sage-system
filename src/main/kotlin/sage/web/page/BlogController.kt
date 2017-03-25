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
import sage.domain.commons.DomainException
import sage.entity.*
import sage.transfer.BlogPreview
import sage.transfer.BlogView
import sage.web.auth.Auth
import sage.web.context.BaseController
import sage.web.context.RenderUtil

@Controller
@RequestMapping("/blogs")
open class BlogController : BaseController() {
  @RequestMapping("/new", method = arrayOf(GET))
  open fun newPage(): ModelAndView {
    val uid = Auth.checkUid()
    val topTags = userService.topTags(uid)
    return ModelAndView("write-blog").addObject("topTags", topTags)
  }

  @RequestMapping("/new", method = arrayOf(POST))
  @ResponseBody
  open fun create(@RequestParam title: String, @RequestParam content: String,
                  @RequestParam contentType: String,
                  @RequestParam(required = false) draftId: Long?): String {
    val uid = Auth.checkUid()
    val tagIds = tagIds()
    val blog = blogService.post(uid, title, content, tagIds, contentType).run { BlogView(this) }
    draftId?.let { Draft.deleteById(it) }
    return "/blogs/${blog.id}"
  }

  @RequestMapping("/{id}/edit", method = arrayOf(GET))
  open fun editPage(@PathVariable id: Long): ModelAndView {
    val uid = Auth.checkUid()
    val blog = Blog.get(id).run { BlogView(this, showInputContent = true) }
    if (uid != blog.author?.id) {
      throw DomainException("无权编辑: User[$uid] cannot edit Blog[${blog.id}]")
    }
    val topTags = userService.filterNewTags(uid, blog.tags)
    return ModelAndView("write-blog")
        .addObject("blog", blog)
        .addObject("existingTags", blog.tags).addObject("topTags", topTags)
  }

  @RequestMapping("/{id}/edit", method = arrayOf(POST))
  @ResponseBody
  open fun edit(@PathVariable id: Long, @RequestParam title: String, @RequestParam content: String,
                @RequestParam contentType: String,
                @RequestParam(required = false) draftId: Long?): String {
    val uid = Auth.checkUid()
    val tagIds = tagIds()
    blogService.edit(uid, id, title, content, tagIds, contentType)
    draftId?.let { Draft.deleteById(it) }
    return "/blogs/$id"
  }

  @RequestMapping("/{id}")
  open fun get(@PathVariable id: Long) : ModelAndView {
    val blog = Blog.get(id).run { BlogView(this) }
    blog.views += 1
    BlogStat.incViews(id)
    val isLiked: Boolean? = Auth.uid()?.let { Liking.find(it, Liking.BLOG, id) != null }
    return ModelAndView("blog").addObject("blog", blog).addObject("isLiked", isLiked)
  }

  @RequestMapping("/{id}/delete", method = arrayOf(POST))
  open fun delete(@PathVariable id: Long): String {
    val uid = Auth.checkUid()
    blogService.delete(uid, id)
    return "redirect:/"
  }

  @RequestMapping
  open fun all(): ModelAndView {
    val pageIndex = pageIndex()
    val pageSize = pageSize()

    val (blogs, pagesCount) = GlobalCaches.blogsCache["/blogs", pageIndex, pageSize]

    return ModelAndView("blogs").addObject("blogs", blogs.map(::BlogPreview))
        .addObject("paginationLinks", RenderUtil.paginationLinks("/blogs", pagesCount, pageIndex))
  }
}