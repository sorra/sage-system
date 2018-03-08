package sage.web.ajax

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import sage.service.TagChangeService
import sage.web.auth.Auth

@RestController
class TagChangesAjaxController
  @Autowired constructor(private val tagChangeService: TagChangeService) {

  @PostMapping("/{id}/move")
  fun move(@PathVariable id: Long, @RequestParam parentId: Long): String {
    tagChangeService.requestMove(Auth.checkUid(), id, parentId)
    return "/tag-changes/$id"
  }

  @PostMapping("/{id}/rename")
  fun rename(@PathVariable id: Long, @RequestParam name: String): String {
    tagChangeService.requestRename(Auth.checkUid(), id, name)
    return "/tag-changes/$id"
  }

  @PostMapping("/{id}/setIntro")
  fun setIntro(@PathVariable id: Long, @RequestParam intro: String): String {
    tagChangeService.requestSetIntro(Auth.checkUid(), id, intro)
    return "/tag-changes/$id"
  }
}