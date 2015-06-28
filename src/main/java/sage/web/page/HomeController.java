package sage.web.page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
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
    Long uid = Auth.checkCuid();
    FrontMap fm = FrontMap.from(model);
    
    fm.put("friends", relationService.friends(uid));
    return "home";
  }

  @RequestMapping("/login")
  public String login() {
    return "login";
  }

  @RequestMapping("/logout")
  public String logout() {
    return "forward:/auth/logout";
  }

  @RequestMapping("/register")
  public String register() {
    return "register";
  }

  @RequestMapping("/not-found")
  public ModelAndView notFound() {
    ModelAndView mv = new ModelAndView("error");
    mv.getModelMap().addAttribute("errorCode", 404).addAttribute("reason", "找不到页面");
    return mv;
  }
}
