package sage.web.page;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import sage.domain.service.RelationService;
import sage.domain.service.UserService;
import sage.entity.Follow;
import sage.transfer.UserCard;
import sage.web.auth.AuthUtil;
import sage.web.context.FrontMap;

@Controller
public class RelationPageController {
  @Autowired
  private UserService userService;
  @Autowired
  private RelationService relationService;

  @RequestMapping("/followings")
  public String followings() {
    return "forward:/followings/" + AuthUtil.checkCurrentUid();
  }

  @RequestMapping("/followings/{userId}")
  public String followings(@PathVariable("userId") long userId, ModelMap model) {
    Long curUid = AuthUtil.checkCurrentUid();
    
    List<UserCard> followings = new ArrayList<>();
    for (Follow follow : relationService.followings(userId)) {
      UserCard following = userService.getUserCard(curUid, follow.getTarget().getId());
      followings.add(following);
    }
    
    FrontMap.from(model).put("followings", followings);
    return "followings";
  }

  @RequestMapping("/followers")
  public String followers() {
    return "forward:/followers/" + AuthUtil.checkCurrentUid();
  }

  @RequestMapping("/followers/{userId}")
  public String followers(@PathVariable("userId") long userId, ModelMap model) {
    Long curUid = AuthUtil.checkCurrentUid();
    
    List<UserCard> followers = new ArrayList<>();
    for (Follow follow : relationService.followers(userId)) {
      UserCard follower = userService.getUserCard(curUid, follow.getSource().getId());
      followers.add(follower);
    }
    
    FrontMap.from(model).put("followers", followers);
    return "followers";
  }
}
