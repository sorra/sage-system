package sage.web.context

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import sage.service.TagService
import sage.service.UserService
import sage.transfer.TagNode
import sage.transfer.UserSelf
import sage.web.model.FrontMap
import sage.web.auth.Auth
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class PageDefaultModelInterceptor @Autowired constructor (
    val userService: UserService,
    val tagService: TagService
) : HandlerInterceptorAdapter() {
  override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any?): Boolean {
    val uri = request.requestURI

    val isPage = arrayOf("/ajax/", "/api/", "/errors/").none { uri.startsWith(it) } && uri != "/error"

    if (isPage) {
      val userSelf = userSelf()
      request.setAttribute("userSelf", userSelf)
      FrontMap.from(request)
          .attr("userSelf", userSelf)
          .attr("tagTree", tagTree())
    }
    return true
  }

  fun userSelf(): UserSelf? = Auth.uid()?.run { silent { userService.getSelf(this) } }

  fun tagTree(): TagNode? = silent { tagService.getTagTree() }

  private val log = LoggerFactory.getLogger(javaClass)

  private inline fun <T> silent(f: () -> T) =
      try {
        f.invoke()
      } catch(e: Exception) {
        log.error("", e)
        null
      }
}