package sage.web.auth

import org.slf4j.LoggerFactory
import org.springframework.web.util.UriUtils
import sage.web.context.WebContexts
import java.io.UnsupportedEncodingException
import java.util.*
import javax.servlet.http.HttpServletRequest

object Auth {
  private val logger = LoggerFactory.getLogger(Auth::class.java)

  fun checkUid(): Long {
    val uid = uid()
    if (uid == null) {
      logger.debug("require login")
      throw RequireLoginException()
    } else
      return uid
  }

  fun uid(): Long? {
    return WebContexts.getSessionAttr(SessionKeys.UID) as Long?
  }

  fun invalidateSession(request: HttpServletRequest) {
    val session = request.getSession(false)
    session?.invalidate()
  }

  internal fun getRedirectGoto(requestLink: String): String {
    return "goto=" + encodeLink(requestLink)
  }

  fun encodeLink(link: String): String {
    try {
      return UriUtils.encodeQueryParam(link, "ISO-8859-1")
    } catch (e: UnsupportedEncodingException) {
      throw RuntimeException(e)
    }

  }

  fun decodeLink(link: String): String {
    try {
      return UriUtils.decode(link, "ISO-8859-1")
    } catch (e: UnsupportedEncodingException) {
      throw RuntimeException(e)
    }

  }
}
