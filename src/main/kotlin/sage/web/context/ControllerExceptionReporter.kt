package sage.web.context

import org.slf4j.LoggerFactory
import org.springframework.beans.TypeMismatchException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.ModelAndView
import sage.domain.commons.BadArgumentException
import sage.domain.commons.DomainException
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import javax.servlet.http.HttpServletRequest

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
open class ControllerExceptionReporter {
  private val log = LoggerFactory.getLogger(javaClass)

  @ExceptionHandler(TypeMismatchException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  open fun typeMismatch(e: TypeMismatchException, request: HttpServletRequest): ModelAndView {
    log.error("URI: {} Exception: {}", request.requestURI, e.toString())
    return errorPage(HttpStatus.BAD_REQUEST, "Parameter type mismatch 参数类型不对")
  }

  @ExceptionHandler(BadArgumentException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  open fun badArgument(e: BadArgumentException, request: HttpServletRequest): ModelAndView {
    log.error("URI: {} Exception: {}", request.requestURI, e.toString())
    return errorPage(HttpStatus.BAD_REQUEST, e.message)
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  open fun httpMethodNotSupported(e: HttpRequestMethodNotSupportedException, request: HttpServletRequest): ModelAndView {
    log.error("URI: {} Exception: {}", request.requestURI, e.toString())
    return errorPage(HttpStatus.METHOD_NOT_ALLOWED, e.message)
  }

  @ExceptionHandler(DomainException::class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  open fun domainRuntimeException(e: DomainException): ModelAndView {
    val msgBuilder = StringBuilder(e.toString())
    val stacks = e.stackTrace
    if (stacks.isNotEmpty()) msgBuilder.append("\n\tat ").append(stacks[0])

    e.cause?.apply {
      val stringWriter = StringWriter()
      printStackTrace(PrintWriter(stringWriter))
      msgBuilder.append("\n\tCaused by: ").append(stringWriter)
    }
    log.error(msgBuilder.toString())
    return errorPage(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @Throws(IOException::class)
  open fun any(e: Throwable, request: HttpServletRequest): ModelAndView {
    log.error("URI: " + request.requestURI + "\nController error: ", e)
    return errorPage(HttpStatus.INTERNAL_SERVER_ERROR, e.javaClass.name)
  }

  private fun errorPage(status: HttpStatus, reason: String?): ModelAndView {
    val mv = ModelAndView("error")
    mv.modelMap.addAttribute("errorCode", status.value()).addAttribute("reason", reason)
    return mv
  }
}
