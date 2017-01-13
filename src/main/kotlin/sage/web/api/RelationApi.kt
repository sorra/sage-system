package sage.web.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import sage.service.NotificationService
import sage.service.RelationService
import sage.web.auth.Auth

@RestController
@RequestMapping(method = arrayOf(RequestMethod.POST))
open class RelationApi
@Autowired constructor(
    private val relationService: RelationService,
    private val notifService: NotificationService) {

  @RequestMapping("/follow/{targetId}")
  open fun follow(@PathVariable targetId: Long,
                  @RequestParam(required = false) reason: String?,
                  @RequestParam("tagIds[]", defaultValue = "") tagIds: Collection<Long>,
                  @RequestParam(defaultValue = "false") includeNew: Boolean,
                  @RequestParam(defaultValue = "false") includeAll: Boolean,
                  @RequestParam(required = false) userTagOffset: Long?) {
    val uid = Auth.checkUid()
    relationService.follow(uid, targetId, reason, tagIds, includeNew, includeAll, userTagOffset)
  }

  @RequestMapping("/unfollow/{targetId}")
  open fun unfollow(@PathVariable targetId: Long) {
    val uid = Auth.checkUid()
    relationService.unfollow(uid, targetId)
  }
}
