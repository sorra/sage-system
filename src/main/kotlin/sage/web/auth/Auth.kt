package sage.web.auth

import org.slf4j.LoggerFactory
import org.springframework.web.util.UriUtils
import sage.entity.LoginPass
import java.io.UnsupportedEncodingException
import java.sql.Timestamp
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

object Auth {
  private val log = LoggerFactory.getLogger(Auth::class.java)

  val TOKEN_NAME = "web_token"

  class AuthPack(val request: HttpServletRequest, val response: HttpServletResponse, var uid: Long? = null)

  val currentAuthPack = ThreadLocal<AuthPack?>()

  fun checkUid(): Long {
    val uid = uid()
    if (uid == null) {
      throw RequireLoginException()
    } else
      return uid
  }

  fun uid(): Long? {
    val authPack = currentAuthPack.get()!!
    if (authPack.uid != null) return authPack.uid

    val token = authPack.request.cookies?.find { it.name == TOKEN_NAME }?.value ?: return null
    val loginPass = LoginPass.byId(token) ?: return null

    if (loginPass.whenToExpire.after(Timestamp(System.currentTimeMillis()))) {
      return loginPass.userId
    } else {
      loginPass.delete()
      return null
    }
  }

  fun login(userId: Long, rememberMe: Boolean) {
    val authPack = currentAuthPack.get()!!

    authPack.request.getSession(false)?.invalidate()
    val tempSession = authPack.request.getSession(true)
    val sessionId = tempSession.id
    tempSession.invalidate()

    val activeSeconds = (if(rememberMe) 7 * 86400 else 86400)
    val whenToExpire = Timestamp(System.currentTimeMillis() + activeSeconds * 1000)

    // sessionId不会重复吧? 若重复就要changeSessionId()重新生成了
    LoginPass(sessionId, userId, whenToExpire).save()
    authPack.uid = userId
    authPack.response.addCookie(Cookie(TOKEN_NAME, sessionId).apply {
      path = "/"
      if(rememberMe) maxAge = activeSeconds
      else maxAge = -1 // transient
    })
  }

  fun logout() {
    val authPack = currentAuthPack.get()!!
    val cookie = authPack.request.cookies?.find { it.name == TOKEN_NAME } ?: return
    authPack.uid = null
    cookie.maxAge = 0 // delete
    LoginPass.deleteById(cookie.value)
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
