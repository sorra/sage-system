package sage.web.context;

import java.io.IOException;
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

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class ControllerExceptionReporter {
  private final Logger log = LoggerFactory.getLogger(getClass());

  @ExceptionHandler(TypeMismatchException.class)
  public ModelAndView typeMismatch(TypeMismatchException e) {
    log.error(e.toString());
    return errorPage(HttpStatus.INTERNAL_SERVER_ERROR, "Parameter type mismatch 参数类型不对");
  }

  @ExceptionHandler(BadArgumentException.class)
  public ModelAndView badArgument(BadArgumentException e, HttpServletResponse response) throws IOException {
    log.error(e.toString());
    return errorPage(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ModelAndView httpMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                     HttpServletRequest request, HttpServletResponse response) throws IOException {
    log.error("URI: " + request.getRequestURI(), e);
    return errorPage(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
  }

  @ExceptionHandler(HibernateJdbcException.class)
  public ModelAndView hibernateJDBCException(HibernateJdbcException e) {
    // Sometimes there is no sql
    log.error("SQL: " + e.getSql() + "\n", e);
    return errorPage(HttpStatus.INTERNAL_SERVER_ERROR, "Database error");
  }

  @ExceptionHandler
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
