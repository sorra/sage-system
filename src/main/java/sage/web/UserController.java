package sage.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sage.service.UserService;
import sage.transfer.UserCard;
import sage.transfer.UserSelf;
import sage.web.auth.Auth;

@RestController
@RequestMapping("/user")
public class UserController {
  @Autowired
  UserService userService;

  @RequestMapping("/self")
  public UserSelf self() {
    Long uid = Auth.checkCuid();
    return userService.getSelf(uid);
  }

  @RequestMapping("/card/{id}")
  public UserCard userCard(@PathVariable Long id) {
    Long uid = Auth.checkCuid();
    return userService.getUserCard(uid, id);
  }

  @RequestMapping("/info/{id}")
  Object userInfo(@PathVariable Long id) {
    throw new UnsupportedOperationException();
  }

  @RequestMapping(value = "/change-intro", method = RequestMethod.POST)
  void changeIntro(@RequestParam String intro) {
    userService.changeIntro(Auth.checkCuid(), intro);
  }

  @RequestMapping(value = "/change-avatar", method = RequestMethod.POST)
  void changeAvatar(@RequestParam MultipartFile photo) {
    userService.changeAvatar(Auth.checkCuid(), null);
  }

}
