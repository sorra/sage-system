package sage.web.error

import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.ModelAndView
import sage.web.auth.Auth
import sage.web.auth.RequireLoginException
import javax.servlet.http.HttpServletRequest

@ControllerAdvice(basePackages = arrayOf("sage.web.page"))
@Order(0)
class PageExceptionHandler {
  @ExceptionHandler(RequireLoginException::class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  fun redirectToLogin(request: HttpServletRequest): ModelAndView {
    var uri = request.requestURI
    if (request.queryString != null) {
      uri += "?" + request.queryString
    }
    return ModelAndView("error-no-login", "location", "/login?" + Auth.getRedirectGoto(uri))
  }
}
