package sage.web.context

import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder

object WebContexts {

  fun current(): RequestAttributes {
    return RequestContextHolder.currentRequestAttributes()
  }

  fun getRequestAttr(name: String): Any? {
    return current().getAttribute(name, RequestAttributes.SCOPE_REQUEST)
  }

  fun setRequestAttr(name: String, value: Any?) {
    current().setAttribute(name, value, RequestAttributes.SCOPE_REQUEST)
  }

  fun getSessionAttr(name: String): Any? {
    return current().getAttribute(name, RequestAttributes.SCOPE_SESSION)
  }

  fun setSessionAttr(name: String, value: Any?) {
    current().setAttribute(name, value, RequestAttributes.SCOPE_SESSION)
  }
}
