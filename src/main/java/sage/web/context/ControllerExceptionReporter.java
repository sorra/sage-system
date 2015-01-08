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
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import sage.domain.commons.BadArgumentException;

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class ControllerExceptionReporter {
  private final Logger log = LoggerFactory.getLogger(getClass());

  @ExceptionHandler(TypeMismatchException.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Parameter type mismatch 参数类型不对")
  public void typeMismatch(TypeMismatchException e) {
    log.error(e.toString());
  }

  @ExceptionHandler(BadArgumentException.class)
  public void badArgument(BadArgumentException e, HttpServletResponse response) throws IOException {
    log.error(e.toString());
    response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public void httpMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                     HttpServletRequest request, HttpServletResponse response) throws IOException {
    log.error("URI: " + request.getRequestURI(), e);
    response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
  }

  @ExceptionHandler
  public void any(Throwable e, HttpServletResponse response) throws IOException {
    log.error("Controller error: ", e);
    response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
  }
}
