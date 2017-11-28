package sage.web.ajax

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import sage.service.TagChangeService
import sage.web.auth.Auth

@RestController
@RequestMapping("/tag-changes", method = arrayOf(RequestMethod.POST))
class TagChangesAjaxController
  @Autowired constructor(private val tagChangeService: TagChangeService) {

  @RequestMapping("/{id}/move")
  fun move(@PathVariable id: Long, @RequestParam parentId: Long): String {
    tagChangeService.requestMove(Auth.checkUid(), id, parentId)
    return "/tag-changes/$id"
  }

  @RequestMapping("/{id}/rename")
  fun rename(@PathVariable id: Long, @RequestParam name: String): String {
    tagChangeService.requestRename(Auth.checkUid(), id, name)
    return "/tag-changes/$id"
  }

  @RequestMapping("/{id}/setIntro")
  fun setIntro(@PathVariable id: Long, @RequestParam intro: String): String {
    tagChangeService.requestSetIntro(Auth.checkUid(), id, intro)
    return "/tag-changes/$id"
  }
}