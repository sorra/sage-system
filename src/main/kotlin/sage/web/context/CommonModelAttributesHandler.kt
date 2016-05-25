package sage.web.context

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute
import sage.service.TagService
import sage.service.UserService
import sage.transfer.UserSelf
import sage.web.auth.Auth

@ControllerAdvice("sage.web.page")
open class CommonModelAttributesHandler @Autowired constructor(
    private val userService: UserService,
    private val tagService: TagService
) {
  private val log = LoggerFactory.getLogger(javaClass)

  @ModelAttribute("userSelf")
  open fun userSelf(): UserSelf? = Auth.uid()?.run { silent { userService.getSelf(this) } }

  @ModelAttribute("userSelfJson")
  open fun userSelfJson(): String? = Auth.uid()?.run { silent { Json.json(userService.getSelf(this)) } }

  @ModelAttribute("tagTreeJson")
  open fun tagTreeJson(): String? = silent { tagService.getTagTreeJson() }

  private inline fun <T> silent(f: () -> T) =
      try {
        f.invoke()
      } catch(e: Exception) {
        log.error("", e)
        null
      }
}
