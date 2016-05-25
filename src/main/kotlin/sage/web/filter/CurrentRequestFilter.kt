package sage.web.filter

import sage.web.auth.Auth
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CurrentRequestFilter : javax.servlet.Filter {

  override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    if (request is HttpServletRequest && response is HttpServletResponse) {
      Auth.currentAuthPack.set(Auth.AuthPack(request, response))
    }
    try {
      chain.doFilter(request, response)
    } finally {
      Auth.currentAuthPack.remove()
    }
  }

  override fun init(filterConfig: FilterConfig?) {}

  override fun destroy() {}
}