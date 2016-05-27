package sage.web.page

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.*
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.ModelAndView
import sage.entity.Blog
import sage.service.BlogService
import sage.service.UserService
import sage.transfer.BlogPreview
import sage.transfer.BlogView
import sage.transfer.TagLabel
import sage.web.auth.Auth
import sage.web.context.FrontMap

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
        .include(FrontMap().attr("id", id))
  }

  @RequestMapping("/{id}/edit", method = arrayOf(POST))
  @ResponseBody
  open fun edit(@PathVariable id: Long, @RequestParam title: String, @RequestParam content: String,
                @RequestParam("tagIds[]", defaultValue = "") tagIds: Set<Long>): String {
    val uid = Auth.checkUid()
    blogService.edit(uid, id, title, content, tagIds)
    return "/blogs/$id"
  }

  @RequestMapping("/{id}/delete", method = arrayOf(POST))
  open fun delete(@PathVariable id: Long): String {
    val uid = Auth.checkUid()
    blogService.delete(uid, id)
    return "redirect:/"
  }

  @RequestMapping("/{id}")
  open fun get(@PathVariable id: Long) : ModelAndView {
    val blog = Blog.get(id).run { BlogView(this) }
    return ModelAndView("blog").addObject("blog", blog)
  }

  @RequestMapping
  open fun all(): ModelAndView {
    val blogs = Blog.all().sortedByDescending { it.whenCreated }.map { BlogPreview(it) }
    return ModelAndView("blogs").addObject("blogs", blogs)
  }
}