package sage.web.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class ControllerExceptionReporter {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @ExceptionHandler
  public void logAny(Throwable e) throws Throwable {
    logger.error("controller error encountered");
    throw e;
  }
}
