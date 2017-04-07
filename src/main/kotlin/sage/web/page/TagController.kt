package sage.web.page

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import sage.entity.Blog
import sage.entity.Tag
import sage.transfer.BlogPreview
import sage.util.Strings
import sage.web.auth.Auth
import sage.web.context.BaseController

@Controller
@RequestMapping("/tags")
open class TagController : BaseController() {

  @RequestMapping("/new", method = arrayOf(RequestMethod.POST))
  @ResponseBody
  open fun create(@RequestParam name: String,
                  @RequestParam(required = false) parentId: Long?,
                  @RequestParam(defaultValue = "false") isCore: Boolean,
                  @RequestParam(defaultValue = "") intro: String): String {
    val uid = Auth.checkUid()
    val tag = tagService.create(uid, name, parentId ?: Tag.ROOT_ID, isCore, intro)
    return "/tags/${tag.id}"
  }

  @RequestMapping("/{id}")
  open fun get(@PathVariable id: Long): ModelAndView {
    val tag = Tag.get(id)
    val blogs = Blog.where().`in`("tags.id", tag.getQueryTags().map { it.id }).findList()
        .sortedByDescending { it.whenCreated }.map(::BlogPreview)

    val (coreTags, nonCoreTags) = tag.children.map { it.toTagLabel() }.partition { it.isCore }
    val relatedTags = null
    val sameNameTags = tagService.getSameNameTags(id).map { it.toTagLabel() }

    frontMap().attr("id", id)

    return ModelAndView("tag-page")
        .addObject("tag", tag)
        .addObject("blogs", blogs)
        .addObject("coreTags", coreTags)
        .addObject("nonCoreTags", nonCoreTags)
        .addObject("relatedTags", relatedTags)
        .addObject("sameNameTags", sameNameTags)
        .addObject("countPendingRequestsOfTagScope", tagChangeService.countPendingRequestsOfTagScope(id))
        .addObject("countPendingRequestsOfTag", tagChangeService.countPendingRequestsOfTag(id))
  }

  @RequestMapping("/{id}/rss")
  fun rss(@PathVariable id: Long): ModelAndView {
    val blogs = Blog.where().`in`("tags", Tag.ref(id)).findList()
    response.contentType = "text/xml"
    blogs.forEach {
      it.content = Strings.escapeXmlInvalidChar(it.content)
    }
    return ModelAndView("rss").addObject("blogs", blogs).addObject("name", Tag.get(id).name)
  }
}