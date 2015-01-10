package sage.web.context;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import sage.domain.service.TagService;
import sage.domain.service.UserService;
import sage.transfer.UserSelf;
import sage.web.auth.Auth;

@ControllerAdvice("sage.web.page")
public class CommonModelAttributesHandler {
  @Autowired
  private UserService userService;
  @Autowired
  private TagService tagService;

  @ModelAttribute("userSelf")
  public UserSelf userSelf() {
    return Auth.cuidOpt().map(userService::getSelf).orElse(null);
  }

  @ModelAttribute("userSelfJson")
  public String userSelfJson() {
    return Auth.cuidOpt().map(cuid -> Json.json(userService.getSelf(cuid))).orElse(null);
  }

  @ModelAttribute("tagTreeJson")
  public String tagTreeJson() {
    return tagService.getTagTreeJson();
  }
}
