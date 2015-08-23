package sage.web.auth;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@Order(0)
public class AuthExceptionHandler {
  @ExceptionHandler(RequireLoginException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ModelAndView redirectToLogin(HttpServletRequest request) {
    String uri = request.getRequestURI();
    if (request.getQueryString() != null) {
      uri += ("?" + request.getQueryString());
    }
    return new ModelAndView("error-no-login", "location", "/login?" + Auth.getRedirectGoto(uri));
  }
}
