package sage.web.ajax

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import sage.service.TagChangeService
import sage.web.auth.Auth

@RestController
@RequestMapping("/tag-change")
class TagChangeAjaxController {
  @Autowired
  private val tagChangeService: TagChangeService? = null

  @PostMapping("/accept")
  fun acceptRequest(@RequestParam requestId: Long) {
    tagChangeService!!.acceptRequest(Auth.checkUid(), requestId)
  }

  @PostMapping("/reject")
  fun rejectRequest(@RequestParam requestId: Long) {
    tagChangeService!!.rejectRequest(Auth.checkUid(), requestId)
  }

  @PostMapping("/cancel")
  fun cancelRequest(@RequestParam requestId: Long) {
    tagChangeService!!.cancelRequest(Auth.checkUid(), requestId)
  }
}
