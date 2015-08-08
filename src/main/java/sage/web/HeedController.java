package sage.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sage.domain.service.HeedService;
import sage.domain.service.TagService;
import sage.domain.service.UserService;
import sage.transfer.FollowList;
import sage.transfer.FollowListLite;
import sage.transfer.TagCard;
import sage.web.auth.Auth;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(method = RequestMethod.POST)
public class HeedController {
  @Autowired
  private HeedService heedService;
  @Autowired
  private UserService userService;
  @Autowired
  private TagService tagService;
  
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

  @RequestMapping(value = "/heeds/tag", method = RequestMethod.GET)
  public List<TagCard> tagHeeds() {
    Long cuid = Auth.checkCuid();
    return heedService.tagHeeds(cuid).stream()
        .map(tagHeed -> tagService.getTagCard(tagHeed.getTag().getId())).collect(toList());
  }

  @RequestMapping(value = "/heeds/follow-list", method = RequestMethod.GET)
  public List<FollowList> followListHeeds() {
    Long cuid = Auth.checkCuid();
    return heedService.followListHeeds(cuid).stream()
        .map(flHeed -> FollowListLite.fromEntity(flHeed.getList()).toFull(userService::getUserLabel, tagService::getTagLabel))
        .collect(toList());
  }
}
