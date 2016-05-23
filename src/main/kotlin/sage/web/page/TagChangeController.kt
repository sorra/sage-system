package sage.web.page

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import sage.service.TagChangeService
import sage.service.TagService
import sage.web.auth.Auth

@Controller
@RequestMapping("/tag-changes/")
open class TagChangeController @Autowired constructor(
    private val tagService: TagService,
    private val tagChangeService: TagChangeService
) {

  @RequestMapping("/{id}")
  open fun requests(@PathVariable id: Long, model: ModelMap): String {
    val cuid = Auth.checkUid()
    model.put("tag", tagService.getTagCard(id))
    model.put("reqs", tagChangeService.getRequestsOfTag(id))
    model.put("userCanTransact", tagChangeService.userCanTransact(cuid))
    model.put("currentUserId", cuid)
    return "tag-requests"
  }

  @RequestMapping("/{id}/scope")
  open fun scopeRequests(@PathVariable id: Long, model: ModelMap): String {
    val cuid = Auth.checkUid()
    model.put("tag", tagService.getTagCard(id))
    model.put("reqs", tagChangeService.getRequestsOfTagScope(id))
    model.put("userCanTransact", tagChangeService.userCanTransact(cuid))
    model.put("currentUserId", cuid)
    return "tag-scope-requests"
  }

  @RequestMapping("{id}/do-change")
  open fun doChange(@PathVariable id: Long, model: ModelMap): String {
    Auth.checkUid()
    model.put("tag", tagService.getTagCard(id))
    return "tag-do-change"
  }
}