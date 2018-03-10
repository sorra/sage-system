package sage.web.filter

import org.slf4j.LoggerFactory
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class AccessLoggingFilter : javax.servlet.Filter {
  private val log = LoggerFactory.getLogger(javaClass)

  override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    var msg: String? = null
    if (request is HttpServletRequest) {
      val query = if(request.queryString != null) "?"+request.queryString else ""
      msg = request.method + " " + request.requestURI + query
    }
    val timeStart = System.currentTimeMillis()
    try {
      chain.doFilter(request, response)
    } finally {
      if (msg != null) {
        val timeCost = System.currentTimeMillis() - timeStart
        log.info("${timeCost}ms $msg")
      }
    }
  }

  override fun init(filterConfig: FilterConfig?) {}

  override fun destroy() {}
}