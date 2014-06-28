package sage.web.page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import sage.domain.service.RelationService;
import sage.domain.service.TagService;
import sage.domain.service.UserService;
import sage.web.auth.Auth;
import sage.web.context.FrontMap;

@Controller
@RequestMapping
public class HomeController {
  @Autowired
  UserService userService;
  @Autowired
  TagService tagService;
  @Autowired
  RelationService relationService;

  @RequestMapping({ "/", "/home" })
  public String home(ModelMap model) {
    Long uid = Auth.checkCurrentUid();
    FrontMap fm = FrontMap.from(model);
    
    fm.put("friends", relationService.friends(uid));
    return "home";
  }

  @RequestMapping("/login")
  public String login() {
    return "login";
  }

  @RequestMapping("/register")
  public String register() {
    return "register";
  }
}
