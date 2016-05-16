package sage.web.page;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sage.service.RelationService;
import sage.service.UserService;
import sage.transfer.UserCard;
import sage.util.Colls;
import sage.web.auth.Auth;
import sage.web.context.FrontMap;

@Controller
public class RelationPageController {
  @Autowired
  private UserService userService;
  @Autowired
  private RelationService relationService;

  @RequestMapping("/followings")
  public String followings() {
    return "forward:/followings/" + Auth.checkCuid();
  }

  @RequestMapping("/followings/{userId}")
  public String followings(@PathVariable long userId, ModelMap model) {
    Long cuid = Auth.checkCuid();

    model.put("thisUser", userService.getUserCard(cuid, userId));

    List<UserCard> followings = Colls.map(relationService.followings(userId),
        fol -> userService.getUserCard(cuid, fol.getTarget().getId()));
    
    FrontMap.from(model).put("followings", followings);
    return "followings";
  }

  @RequestMapping("/followers")
  public String followers() {
    return "forward:/followers/" + Auth.checkCuid();
  }

  @RequestMapping("/followers/{userId}")
  public String followers(@PathVariable long userId, ModelMap model) {
    Long cuid = Auth.checkCuid();

    model.put("thisUser", userService.getUserCard(cuid, userId));
    
    List<UserCard> followers = Colls.map(relationService.followers(userId),
        fol -> userService.getUserCard(cuid, fol.getSource().getId()));
    
    FrontMap.from(model).put("followers", followers);
    return "followers";
  }
}
