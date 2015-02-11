package sage.web.page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import sage.domain.service.UserService;
import sage.web.auth.Auth;

@Controller
public class UserPageController {
  @Autowired
  private UserService userService;

  @RequestMapping(value = "/user-info", method = RequestMethod.GET)
  String info(ModelMap model) {
    Long cuid = Auth.checkCuid();
    model.put("user", userService.getUserLabel(cuid));
    return "user-info";
  }

  @RequestMapping(value = "/user-info", method = RequestMethod.POST)
  String changeInfo(@RequestParam String intro, @RequestParam(required = false) MultipartFile avatar) {
    Long cuid = Auth.checkCuid();
    userService.changeInfo(cuid, intro, "todo");
    return "redirect:/user-info";
  }
}
