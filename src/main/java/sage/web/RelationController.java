package sage.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sage.domain.service.RelationService;
import sage.transfer.FollowListLite;
import sage.web.auth.Auth;
import sage.web.context.Json;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping(method = RequestMethod.POST)
public class RelationController {
  @Autowired
  private RelationService relationService;

  @RequestMapping("/follow/{targetId}")
  public void follow(@PathVariable Long targetId,
      @RequestParam(value = "reason", required = false) String reason,
      @RequestParam(value = "tagIds[]", required = false) Collection<Long> tagIds) {
    Long uid = Auth.checkCurrentUid();

    if (tagIds == null) {
      tagIds = Collections.emptyList();
    }
    relationService.follow(uid, targetId, reason, tagIds);
    // Send "followed" notification
  }

  @RequestMapping("/unfollow/{targetId}")
  public void unfollow(@PathVariable Long targetId) {
    Long uid = Auth.checkCurrentUid();

    relationService.unfollow(uid, targetId);
  }
  
  @RequestMapping("/apply-follows")
  public void applyFollows(@RequestParam String listLite) {
    Long uid = Auth.checkCurrentUid();
    
    FollowListLite fcl = Json.object(listLite, FollowListLite.class);
    //TODO Be able to unapply this list
    relationService.applyFollows(uid, fcl);
  }
}
