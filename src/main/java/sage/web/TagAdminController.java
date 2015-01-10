package sage.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sage.domain.service.TagChangeService;
import sage.web.auth.Auth;

@RestController
@RequestMapping("/tag-admin")
public class TagAdminController {
  @Autowired
  private TagChangeService tagChangeService;

  @RequestMapping(value = "/accept", method = RequestMethod.POST)
  public void acceptRequest(@RequestParam Long requestId) {
    tagChangeService.acceptRequest(Auth.checkCurrentUid(), requestId);
  }

  @RequestMapping(value = "/reject", method = RequestMethod.POST)
  public void rejectRequest(@RequestParam Long requestId) {
    tagChangeService.rejectRequest(Auth.checkCurrentUid(), requestId);
  }
}
