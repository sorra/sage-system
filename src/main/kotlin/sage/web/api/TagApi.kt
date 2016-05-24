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

  @RequestMapping("/{id}/move")
  open fun move(@PathVariable id: Long, @RequestParam parentId: Long): String {
    tagChangeService.requestMove(Auth.checkUid(), id, parentId)
    return "/tag-changes/$id"
  }

  @RequestMapping("/{id}/rename")
  open fun rename(@PathVariable id: Long, @RequestParam name: String): String {
    tagChangeService.requestRename(Auth.checkUid(), id, name)
    return "/tag-changes/$id"
  }

  @RequestMapping("/{id}/setIntro")
  open fun setIntro(@PathVariable id: Long, @RequestParam intro: String): String {
    tagChangeService.requestSetIntro(Auth.checkUid(), id, intro)
    return "/tag-changes/$id"
  }
}
