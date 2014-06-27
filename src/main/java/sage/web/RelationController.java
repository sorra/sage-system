package sage.web;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sage.domain.service.RelationService;
import sage.entity.nosql.FollowCatalogLite;
import sage.web.auth.AuthUtil;
import sage.web.context.JsonUtil;

@Controller
@RequestMapping
public class RelationController {
  @Autowired
  private RelationService relationService;

  @RequestMapping("/follow/{id}")
  @ResponseBody
  public void follow(@PathVariable("id") Long targetId,
      @RequestParam(value = "tagIds[]", required = false) Collection<Long> tagIds) {
    Long uid = AuthUtil.checkCurrentUid();

    if (tagIds == null) {
      tagIds = Collections.EMPTY_LIST;
    }
    relationService.follow(uid, targetId, tagIds);
    // Send "followed" notification
  }

  @RequestMapping("/editfollow/{id}")
  @ResponseBody
  public void editFollow(@PathVariable("id") Long targetId,
      @RequestParam(value = "tagIds[]", required = false) Collection<Long> tagIds) {
    Long uid = AuthUtil.checkCurrentUid();

    if (tagIds == null) {
      tagIds = Collections.EMPTY_LIST;
    }
    relationService.follow(uid, targetId, tagIds);
  }

  @RequestMapping("/unfollow/{id}")
  @ResponseBody
  public void unfollow(@PathVariable("id") Long targetId) {
    Long uid = AuthUtil.checkCurrentUid();

    relationService.unfollow(uid, targetId);
  }
  
  @RequestMapping("/apply-follows")
  @ResponseBody
  public void applyFollows(@RequestParam("catalogLite") String catalogLite) {
    Long uid = AuthUtil.checkCurrentUid();
    
    FollowCatalogLite fcl = JsonUtil.object(catalogLite, FollowCatalogLite.class);
    //TODO Be able to unapply this catalog
    relationService.applyFollows(uid, fcl);
  }
}
