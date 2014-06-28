package sage.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sage.domain.service.RelationService;
import sage.entity.nosql.FollowCatalogLite;
import sage.web.auth.AuthUtil;
import sage.web.context.JsonUtil;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping(method = RequestMethod.POST)
public class RelationController {
  @Autowired
  private RelationService relationService;

  @RequestMapping("/follow/{targetId}")
  public void follow(@PathVariable Long targetId,
      @RequestParam(value = "tagIds[]", required = false) Collection<Long> tagIds) {
    Long uid = AuthUtil.checkCurrentUid();

    if (tagIds == null) {
      tagIds = Collections.emptyList();
    }
    relationService.follow(uid, targetId, tagIds);
    // Send "followed" notification
  }

  @RequestMapping("/editfollow/{targetId}")
  public void editFollow(@PathVariable Long targetId,
      @RequestParam(value = "tagIds[]", required = false) Collection<Long> tagIds) {
    Long uid = AuthUtil.checkCurrentUid();

    if (tagIds == null) {
      tagIds = Collections.emptyList();
    }
    relationService.follow(uid, targetId, tagIds);
  }

  @RequestMapping("/unfollow/{targetId}")
  public void unfollow(@PathVariable Long targetId) {
    Long uid = AuthUtil.checkCurrentUid();

    relationService.unfollow(uid, targetId);
  }
  
  @RequestMapping("/apply-follows")
  public void applyFollows(@RequestParam String catalogLite) {
    Long uid = AuthUtil.checkCurrentUid();
    
    FollowCatalogLite fcl = JsonUtil.object(catalogLite, FollowCatalogLite.class);
    //TODO Be able to unapply this catalog
    relationService.applyFollows(uid, fcl);
  }
}
