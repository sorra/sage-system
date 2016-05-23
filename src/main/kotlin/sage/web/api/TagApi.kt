package sage.web.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import sage.entity.Tag
import sage.service.TagChangeService
import sage.service.TagService
import sage.transfer.TagCard
import sage.transfer.TagChangeRequestCard
import sage.transfer.TagNode
import sage.web.auth.Auth

@RestController
@RequestMapping("/tag")
open class TagApi
@Autowired constructor(
    private val tagService: TagService,
    private val tagChangeService: TagChangeService
) {

  @RequestMapping("/card/{id}")
  open fun tagCard(@PathVariable id: Long) = tagService.getTagCard(id)

  @RequestMapping("/tree")
  open fun tagTree() = tagService.getTagTree()

  @RequestMapping("/new")
  open fun create(@RequestParam name: String,
                  @RequestParam(required = false) parentId: Long?,
                  @RequestParam(required = false) intro: String?): Long {
    Auth.checkUid()
    return tagService.create(name, parentId ?: Tag.ROOT_ID, intro ?: "").id
  }

  @RequestMapping("/{id}/move")
  open fun move(@PathVariable id: Long, @RequestParam parentId: Long) {
    tagChangeService.requestMove(Auth.checkUid(), id, parentId)
  }

  @RequestMapping("/{id}/rename")
  open fun rename(@PathVariable id: Long, @RequestParam name: String) {
    tagChangeService.requestRename(Auth.checkUid(), id, name)
  }

  @RequestMapping("/{id}/setIntro")
  open fun setIntro(@PathVariable id: Long, @RequestParam intro: String) {
    tagChangeService.requestSetIntro(Auth.checkUid(), id, intro)
  }

  @RequestMapping("/{id}/requests")
  open fun requests(@PathVariable id: Long): Collection<TagChangeRequestCard> {
    Auth.checkUid()
    return tagChangeService.getRequestsOfTag(id)
  }

  @RequestMapping("/{id}/scope-requests")
  open fun scopeRequests(@PathVariable id: Long): Collection<TagChangeRequestCard> {
    Auth.checkUid()
    return tagChangeService.getRequestsOfTagScope(id)
  }
}
