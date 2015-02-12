package sage.web.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sage.domain.commons.BadArgumentException;
import sage.domain.commons.DomainRuntimeException;
import sage.domain.service.UserService;
import sage.entity.User;

@Controller
@RequestMapping("/auth")
public class AuthController {
  private final static Logger log = LoggerFactory.getLogger(AuthController.class);

  @Autowired
  UserService userService;

  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public String login(HttpServletRequest request,
      @RequestParam("email") String email,
      @RequestParam("password") String password) {
    if (email.isEmpty() || password.isEmpty()) {
      throw new DomainRuntimeException("Empty input!");
    }
    log.info("Login email: {}", email);
    Auth.invalidateSession(request);

    String referer = request.getHeader("referer");
    log.debug("Referer: {}", referer);

    final String destContext = "?goto=";
    int idx = referer.lastIndexOf(destContext);
    String dest = idx < 0 ? null : referer.substring(
        idx + destContext.length(), referer.length());
    if (dest != null && dest.contains(":")) {
      log.info("XSS URL = " + dest);
      dest = null; // Escape cross-site url
    }

    User user = userService.login(email, password);
    if (user != null) {
      HttpSession sesison = request.getSession(true);
      sesison.setAttribute(SessionKeys.UID, user.getId());
      log.info("User {} logged in.", user);
      if (dest == null) {
        return "redirect:/";
      }
      else {
        return "redirect:" + Auth.decodeLink(dest);
      }
    }
    else {
      log.info("{} login failed.", email);
      if (dest == null) {
        return "redirect:/login";
      }
      else {
        return "redirect:/login?" + Auth.getRedirectGoto(dest);
      }
    }
  }

  @RequestMapping("/logout")
  public String logout(HttpServletRequest request) {
    log.info("Logout uid: ", Auth.cuid());
    Auth.invalidateSession(request);
    return "redirect:/login";
  }

  @RequestMapping(value = "/register", method = RequestMethod.POST)
  public String register(HttpServletRequest request,
      @RequestParam("email") String email,
      @RequestParam("password") String password,
      @RequestParam(value = "repeatPassword", required = false) String repeatPassword) {
    log.info("Try to register email: {}", email);
    
    if (email.length() > 50) {
      throw EMAIL_TOO_LONG;
    }
    int idxOfAt = email.indexOf('@');
    if (idxOfAt <= 0 || email.indexOf('.', idxOfAt) <= 0) {
      throw EMAIL_WRONG_FORMAT;
    }

    if (password.length() < 8) {
      throw PASSWORD_TOO_SHORT;
    }
    if (password.length() > 20) {
      throw PASSWORD_TOO_LONG;
    }
    if (repeatPassword != null && !repeatPassword.equals(password)) {
      throw REPEAT_PASSWORD_NOT_MATCH;
    }
    
    userService.register(new User(email, password));
    login(request, email, password);
    return "redirect:/people";
  }
  
  private static final BadArgumentException
      EMAIL_TOO_LONG = new BadArgumentException("Email不能超过50个字符"),
      EMAIL_WRONG_FORMAT = new BadArgumentException("Email格式错误"),
      PASSWORD_TOO_SHORT = new BadArgumentException("密码太短，至少要8位"),
      PASSWORD_TOO_LONG = new BadArgumentException("密码太长，不要超过20位"),
      REPEAT_PASSWORD_NOT_MATCH = new BadArgumentException("两次输入的密码不一致");
}
