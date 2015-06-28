package sage.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sage.domain.service.TagChangeService;
import sage.web.auth.Auth;

@RestController
@RequestMapping(value = "/tag-change", method = RequestMethod.POST)
public class TagChangeController {
  @Autowired
  private TagChangeService tagChangeService;

  @RequestMapping("/accept")
  public void acceptRequest(@RequestParam Long requestId) {
    tagChangeService.acceptRequest(Auth.checkCuid(), requestId);
  }

  @RequestMapping("/reject")
  public void rejectRequest(@RequestParam Long requestId) {
    tagChangeService.rejectRequest(Auth.checkCuid(), requestId);
  }

  @RequestMapping("/cancel")
  public void cancelRequest(@RequestParam Long requestId) {
    tagChangeService.cancelRequest(Auth.checkCuid(), requestId);
  }
}
