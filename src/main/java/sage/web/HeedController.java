package sage.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sage.domain.service.HeedService;
import sage.web.auth.Auth;

@RestController
@RequestMapping(method = RequestMethod.POST)
public class HeedController {
  @Autowired
  private HeedService heedService;
  
  @RequestMapping("/heed/tag/{id}")
  public void heedTag(@PathVariable long tagId) {
    Long uid = Auth.checkCuid();
    heedService.heedTag(uid, tagId);
  }
  
  @RequestMapping("/unheed/tag/{id}")
  public void unheedTag(@PathVariable long tagId) {
    Long uid = Auth.checkCuid();
    heedService.unheedTag(uid, tagId);
  }

  @RequestMapping("/heed/follow-list/{id}")
  public void heedFollowList(@RequestParam long id) {
    Long cuid = Auth.checkCuid();
    heedService.heedFollowList(cuid, id);
  }

  @RequestMapping("/unheed/follow-list/{id}")
  public void unheedFollowList(@RequestParam long id) {
    Long cuid = Auth.checkCuid();
    heedService.unheedFollowList(cuid, id);
  }
}
