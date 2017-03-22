package sage.web.ajax

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import sage.service.TagChangeService
import sage.web.auth.Auth

@RestController
@RequestMapping("/tag-change", method = arrayOf(RequestMethod.POST))
class TagChangeAjaxController {
  @Autowired
  private val tagChangeService: TagChangeService? = null

  @RequestMapping("/accept")
  fun acceptRequest(@RequestParam requestId: Long) {
    tagChangeService!!.acceptRequest(Auth.checkUid(), requestId)
  }

  @RequestMapping("/reject")
  fun rejectRequest(@RequestParam requestId: Long) {
    tagChangeService!!.rejectRequest(Auth.checkUid(), requestId)
  }

  @RequestMapping("/cancel")
  fun cancelRequest(@RequestParam requestId: Long) {
    tagChangeService!!.cancelRequest(Auth.checkUid(), requestId)
  }
}
