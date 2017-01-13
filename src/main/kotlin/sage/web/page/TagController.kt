package sage.web.page

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import sage.entity.Blog
import sage.entity.Tag
import sage.service.TagChangeService
import sage.service.TagService
import sage.transfer.BlogPreview
import sage.transfer.TagLabel
import sage.web.auth.Auth
import sage.web.context.FrontMap

@Controller
@RequestMapping("/tags")
open class TagController @Autowired constructor(
    private val tagService: TagService,
    private val tagChangeService: TagChangeService
) {

  @RequestMapping("/new", method = arrayOf(RequestMethod.POST))
  @ResponseBody
  open fun create(@RequestParam name: String,
                  @RequestParam(required = false) parentId: Long?,
                  @RequestParam(required = false) isCore: Boolean?,
                  @RequestParam(required = false) intro: String?): String {
    val uid = Auth.checkUid()
    val tag = tagService.create(uid, name, parentId ?: Tag.ROOT_ID, isCore ?: false, intro ?: "")
    return "/tags/${tag.id}"
  }

  @RequestMapping("/{id}")
  open fun get(@PathVariable id: Long): ModelAndView {
    val tag = Tag.get(id)
    val blogs = Blog.where().`in`("tags.id", tag.getQueryTags().map { it.id }).findList()
        .sortedByDescending { it.whenCreated }.map(::BlogPreview)

    val (coreTags, nonCoreTags) = tag.children.map(::TagLabel).partition { it.isCore }
    val relatedTags = null
    val sameNameTags = tagService.getSameNameTags(id).map(::TagLabel)

    return ModelAndView("tag-page").addObject("tag", tag).addObject("blogs", blogs)
        .addObject("coreTags", coreTags).addObject("nonCoreTags", nonCoreTags)
        .addObject("relatedTags", relatedTags).addObject("sameNameTags", sameNameTags)
        .addObject("countPendingRequestsOfTagScope", tagChangeService.countPendingRequestsOfTagScope(id))
        .addObject("countPendingRequestsOfTag", tagChangeService.countPendingRequestsOfTag(id))
        .include(FrontMap().attr("id", id))
  }
}