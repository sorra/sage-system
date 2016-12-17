package sage.web.error

import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import sage.domain.commons.DomainException
import sage.web.auth.RequireLoginException
import javax.servlet.http.HttpServletRequest

@ControllerAdvice(basePackages = arrayOf("sage.web.ajax"))
@Order(0)
class AjaxExceptionHandler {
  @ExceptionHandler(RequireLoginException::class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ResponseBody
  fun requireLogin(request: HttpServletRequest) = AjaxError("请登录")

  @ExceptionHandler(DomainException::class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  fun domainError(exception: DomainException) = AjaxError(exception.message ?: "未知错误")
}
