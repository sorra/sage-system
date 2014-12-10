package sage.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import sage.domain.service.UserService;
import sage.transfer.UserCard;
import sage.transfer.UserSelf;
import sage.web.auth.Auth;

@Controller
@RequestMapping("/user")
public class UserController {
  @Autowired
  UserService userService;

  @RequestMapping("/self")
  @ResponseBody
  public UserSelf self() {
    Long uid = Auth.checkCurrentUid();
    return userService.getSelf(uid);
  }

  @RequestMapping("/card/{id}")
  @ResponseBody
  public UserCard userCard(@PathVariable Long id) {
    Long uid = Auth.checkCurrentUid();
    return userService.getUserCard(uid, id);
  }

  @RequestMapping("/info/{id}")
  public ModelAndView userInfo(@PathVariable Long id) {
    throw new UnsupportedOperationException();
  }
}
