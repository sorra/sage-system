package sage.web;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sage.domain.service.NotifService;
import sage.domain.service.RelationService;
import sage.transfer.FollowListLite;
import sage.web.auth.Auth;
import sage.web.context.Json;

@RestController
@RequestMapping(method = RequestMethod.POST)
public class RelationController {
  @Autowired
  private RelationService relationService;
  @Autowired
  private NotifService notifService;

  @RequestMapping("/follow/{targetId}")
  public void follow(@PathVariable Long targetId,
      @RequestParam(required = false) String reason,
      @RequestParam(value = "tagIds[]", required = false) Collection<Long> tagIds,
      @RequestParam(required = false) Boolean includeNew,
      @RequestParam(required = false) Boolean includeAll) {
    Long cuid = Auth.checkCuid();

    if (tagIds == null) {
      tagIds = Collections.emptyList();
    }
    relationService.follow(cuid, targetId, reason, tagIds, boolValue(includeNew), boolValue(includeAll));
    // Send "followed" notification
    notifService.followed(targetId, cuid);
  }

  @RequestMapping("/unfollow/{targetId}")
  public void unfollow(@PathVariable Long targetId) {
    Long uid = Auth.checkCuid();

    relationService.unfollow(uid, targetId);
  }
  
  @RequestMapping("/apply-follows")
  public void applyFollows(@RequestParam String listLite) {
    Long uid = Auth.checkCuid();
    
    FollowListLite fcl = Json.object(listLite, FollowListLite.class);
    //TODO Be able to unapply this list
    relationService.applyFollows(uid, fcl);
  }

  private boolean boolValue(Boolean o) {
    return o == null ? false : o.booleanValue();
  }
}
