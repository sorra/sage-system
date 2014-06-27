package sage.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import sage.domain.service.HeedService;
import sage.web.auth.AuthUtil;

@Controller
@RequestMapping(method = RequestMethod.POST)
public class HeedController {
  @Autowired
  private HeedService heedService;
  
  @RequestMapping("/heed/tag/{id}")
  @ResponseBody
  public void heedTag(@PathVariable("id") long tagId) {
    Long uid = AuthUtil.checkCurrentUid();
    heedService.heedTag(uid, tagId);
  }
  
  @RequestMapping("/unheed/tag/{id}")
  @ResponseBody
  public void unheedTag(@PathVariable("id") long tagId) {
    Long uid = AuthUtil.checkCurrentUid();
    heedService.unheedTag(uid, tagId);
  }
}
