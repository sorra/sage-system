package sage.web.page

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import sage.domain.commons.DomainException
import sage.entity.Blog
import sage.entity.Draft
import sage.entity.User
import sage.service.UserService
import sage.transfer.BlogView
import sage.web.auth.Auth

@Controller
@RequestMapping("/drafts")
open class DraftsController @Autowired constructor(private val userService: UserService) {
  @RequestMapping
  open fun show(): ModelAndView {
    val uid = Auth.checkUid()
    val drafts = Draft.where().eq("owner", User.ref(uid)).findList()
    return ModelAndView("drafts").addObject("drafts", drafts)
  }

  @RequestMapping("/{id}")
  open fun draft(@PathVariable id: Long): ModelAndView {
    val uid = Auth.checkUid()
    Draft.byId(id)?.let { draft ->
      if (draft.targetId > 0) {
        val blog = Blog.get(draft.targetId).let(::BlogView).apply {
          title = draft.title
          content = draft.content
        }
        val topTags = userService.filterNewTags(uid, blog.tags)
        return ModelAndView("write-blog").addObject("blog", blog)
            .addObject("existingTags", blog.tags).addObject("topTags", topTags)
            .addObject("draftId", id)
      } else {
        val newBlog = BlogView().apply {
          title = draft.title
          content = draft.content
        }
        val topTags = userService.topTags(uid)
        return ModelAndView("write-blog").addObject("blog", newBlog)
            .addObject("topTags", topTags)
            .addObject("draftId", id)
      }
    } ?: throw DomainException("Draft[$id] does not exist")
  }
}