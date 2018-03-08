package sage.web.ajax

import org.springframework.web.bind.annotation.*
import sage.web.auth.Auth
import sage.web.context.BaseController

@RestController
class RelationAjaxController : BaseController() {

  @PostMapping("/follow/{targetId}")
  fun follow(@PathVariable targetId: Long,
                  @RequestParam(required = false) reason: String?,
                  @RequestParam(defaultValue = "false") includeNew: Boolean,
                  @RequestParam(defaultValue = "false") includeAll: Boolean,
                  @RequestParam(required = false) userTagOffset: Long?) {
    val uid = Auth.checkUid()
    val tagIds = tagIds()
    relationService.follow(uid, targetId, reason, tagIds, includeNew, includeAll, userTagOffset)
  }

  @PostMapping("/unfollow/{targetId}")
  fun unfollow(@PathVariable targetId: Long) {
    val uid = Auth.checkUid()
    relationService.unfollow(uid, targetId)
  }
}
