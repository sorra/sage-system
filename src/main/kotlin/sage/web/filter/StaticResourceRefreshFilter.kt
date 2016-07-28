package sage.web.filter

import sage.util.Utils
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper


class StaticResourceRefreshFilter : javax.servlet.Filter {

  override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    if (request is HttpServletRequest) {
      val uri = request.requestURI
      val idxOfV = uri.indexOf("_v_")
      if (idxOfV > 0 && Utils.isStaticResource(uri)) {
        val idxOfDot = uri.lastIndexOf(".")
        val realUri = uri.substring(0, idxOfV) + uri.substring(idxOfDot)
        request.getRequestDispatcher(realUri).forward(request, response)
        return
      }
    }
    chain.doFilter(request, response)
  }

  override fun init(filterConfig: FilterConfig?) {}

  override fun destroy() {}
}