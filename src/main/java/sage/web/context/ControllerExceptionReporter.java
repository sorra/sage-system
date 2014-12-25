package sage.web.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class ControllerExceptionReporter {
  private final Logger log = LoggerFactory.getLogger(getClass());

  @ExceptionHandler(TypeMismatchException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Parameter type mismatch")
  public void typeMismatch(TypeMismatchException e) {
    log.error(e.toString());
  }

  @ExceptionHandler
  public void any(Throwable e) throws Throwable {
    log.error("controller error encountered");
    throw e;
  }
}
