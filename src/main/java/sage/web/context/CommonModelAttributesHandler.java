package sage.web.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import sage.domain.service.TagService;
import sage.domain.service.UserService;
import sage.web.auth.Auth;

@ControllerAdvice
public class CommonModelAttributesHandler {
  @Autowired
  private UserService userService;
  @Autowired
  private TagService tagService;

  @ModelAttribute("userSelfJson")
  public String userSelfJson() {
    Long uid = Auth.currentUid();
    return uid == null ? null : Json.json(userService.getSelf(uid));
  }

  @ModelAttribute("tagTreeJson")
  public String tagTreeJson() {
    return tagService.getTagTreeJson();
  }
}
