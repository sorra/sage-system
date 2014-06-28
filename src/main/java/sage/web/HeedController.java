package sage.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sage.domain.service.HeedService;
import sage.web.auth.Auth;

@RestController
@RequestMapping(method = RequestMethod.POST)
public class HeedController {
  @Autowired
  private HeedService heedService;
  
  @RequestMapping("/heed/tag/{id}")
  public void heedTag(@PathVariable long tagId) {
    Long uid = Auth.checkCurrentUid();
    heedService.heedTag(uid, tagId);
  }
  
  @RequestMapping("/unheed/tag/{id}")
  public void unheedTag(@PathVariable long tagId) {
    Long uid = Auth.checkCurrentUid();
    heedService.unheedTag(uid, tagId);
  }
}
