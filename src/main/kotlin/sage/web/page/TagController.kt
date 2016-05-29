package sage.web.page

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import sage.entity.Blog
import sage.entity.Tag
import sage.service.TagChangeService
import sage.service.TagService
import sage.service.TopicService
import sage.transfer.BlogPreview
import sage.transfer.TagLabel
import sage.web.auth.Auth
import sage.web.context.FrontMap

@Controller
@RequestMapping("/tags")
open class TagController @Autowired constructor(
    private val tagService: TagService,
    private val topicService: TopicService,
    private val tagChangeService: TagChangeService
) {

  @RequestMapping("/new", method = arrayOf(RequestMethod.POST))
  @ResponseBody
  open fun create(@RequestParam name: String,
                  @RequestParam(required = false) parentId: Long?,
                  @RequestParam(required = false) intro: String?): String {
    Auth.checkUid()
    val tag = tagService.create(name, parentId ?: Tag.ROOT_ID, intro ?: "")
    return "/tags/${tag.id}"
  }

  @RequestMapping("/{id}")
  open fun get(@PathVariable id: Long): ModelAndView {
    val tag = Tag.get(id)
    val topics = topicService.byTags(listOf(id)).map(topicService.asTopicPreview)
    val blogs = Blog.where().filterMany("tags").`in`("id", tag.getQueryTags().map { it.id }).findList().map { BlogPreview(it) }

    val (coreTags, nonCoreTags) = tag.children.map { TagLabel(it) }.partition { it.isCore }
    val sameNameTags = tagService.getSameNameTags(id).map { TagLabel(it) }
    val relatedTags = null

    return ModelAndView("tag-page").addObject("tag", tag).addObject("topics", topics).addObject("blogs", blogs)
        .addObject("coreTags", coreTags).addObject("nonCoreTags", nonCoreTags)
        .addObject("sameNameTags", sameNameTags).addObject("relatedTags", relatedTags)
        .addObject("countPendingRequestsOfTagScope", tagChangeService.countPendingRequestsOfTagScope(id))
        .addObject("countPendingRequestsOfTag", tagChangeService.countPendingRequestsOfTag(id))
        .include(FrontMap().attr("id", id))
  }
}