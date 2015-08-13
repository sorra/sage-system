package sage.web.context;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.orm.hibernate4.HibernateJdbcException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import sage.domain.commons.BadArgumentException;
import sage.domain.commons.DomainRuntimeException;

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class ControllerExceptionReporter {
  private final Logger log = LoggerFactory.getLogger(getClass());

  @ExceptionHandler(TypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ModelAndView typeMismatch(TypeMismatchException e) {
    log.error(e.toString());
    return errorPage(HttpStatus.BAD_REQUEST, "Parameter type mismatch 参数类型不对");
  }

  @ExceptionHandler(BadArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ModelAndView badArgument(BadArgumentException e, HttpServletResponse response) throws IOException {
    log.error(e.toString());
    return errorPage(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  public ModelAndView httpMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                     HttpServletRequest request, HttpServletResponse response) throws IOException {
    log.error("URI: " + request.getRequestURI(), e);
    return errorPage(HttpStatus.METHOD_NOT_ALLOWED, e.getMessage());
  }

  @ExceptionHandler(DomainRuntimeException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ModelAndView domainRuntimeException(DomainRuntimeException e) {
    StringBuilder msgBuilder = new StringBuilder(e.toString());
    StackTraceElement[] stacks = e.getStackTrace();
    if (stacks.length > 0) msgBuilder.append("\n\tat ").append(stacks[0]);

    if (e.getCause() != null) {
      StringWriter stringWriter = new StringWriter();
      e.getCause().printStackTrace(new PrintWriter(stringWriter));
      msgBuilder.append("\n\tCaused by: ").append(stringWriter);
    }

    log.error(msgBuilder.toString());
    return errorPage(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
  }

  @ExceptionHandler(HibernateJdbcException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ModelAndView hibernateJDBCException(HibernateJdbcException e) {
    // Sometimes there is no sql
    log.error("SQL: " + e.getSql() + "\n", e);
    return errorPage(HttpStatus.INTERNAL_SERVER_ERROR, "Database error");
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ModelAndView any(Throwable e, HttpServletRequest request, HttpServletResponse response) throws IOException {
    log.error("URI: " + request.getRequestURI() + "\nController error: ", e);
    return errorPage(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
  }

  private ModelAndView errorPage(HttpStatus status, String reason) {
    ModelAndView mv = new ModelAndView("error");
    mv.getModelMap().addAttribute("errorCode", status.value()).addAttribute("reason", reason);
    return mv;
  }
}
