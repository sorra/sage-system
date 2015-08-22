package sage.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import sage.domain.service.ListService;
import sage.domain.service.RelationService;
import sage.domain.service.TagService;
import sage.domain.service.UserService;
import sage.entity.Tag;
import sage.transfer.*;
import sage.util.Colls;
import sage.web.auth.Auth;
import sage.web.context.Json;

@RestController
@RequestMapping("/list")
public class ListController {
  @Autowired
  private ListService listService;
  @Autowired
  private RelationService relationService;
  @Autowired
  private UserService userService;
  @Autowired
  private TagService tagService;
  
  @RequestMapping(value = "/resource/{id}", method = RequestMethod.GET)
  public ResourceList getResourceList(@PathVariable Long id) {
    Auth.checkCuid();
    return listService.getResourceList(id);
  }
  
  @RequestMapping(value = "/resource/{id}", method = RequestMethod.POST)
  public Boolean updateResourceList(@PathVariable Long id, @RequestParam String list) {
    Long uid = Auth.checkCuid();
    
    ResourceList rc = Json.object(list, ResourceList.class);
    Assert.isTrue(rc.getOwnerId().equals(uid));
    Assert.isTrue(rc.getId().equals(id));
    listService.updateResourceList(rc, uid);
    return true;
  }

  @RequestMapping(value = "/resource/add", method = RequestMethod.POST)
  public Long addResourceList(@RequestParam String list) {
    Long uid = Auth.checkCuid();
    
    ResourceList rc = Json.object(list, ResourceList.class);
    return listService.addResourceList(rc, uid);
  }
  
  @RequestMapping(value = "/follow/{id}", method = RequestMethod.GET)
  public FollowList getFollowList(@PathVariable Long id) {
    Auth.checkCuid();
    return listService.getFollowList(id);
  }
  
  @RequestMapping(value = "/follow/{id}", method = RequestMethod.POST)
  public Boolean updateFollowList(@PathVariable Long id, @RequestParam String listLite) {
    Long uid = Auth.checkCuid();
    
    FollowListLite fcLite = Json.object(listLite, FollowListLite.class);
    Assert.isTrue(fcLite.getOwnerId().equals(uid));
    Assert.isTrue(fcLite.getId().equals(id));
    listService.updateFollowList(fcLite, uid);
    return true;
  }

  @RequestMapping(value = "/follow/add", method = RequestMethod.POST)
  public Long addFollowList(@RequestParam String listLite) {
    Long uid = Auth.checkCuid();

    FollowListLite fcLite = Json.object(listLite, FollowListLite.class);
    fcLite.setOwnerId(uid);
    return listService.addFollowList(fcLite, uid);
  }

  @RequestMapping(value = "/follow/expose-all", method = RequestMethod.POST)
  public Long exposeAllOfFollow() {
    Long cuid = Auth.checkCuid();

    List<FollowInfoLite> follows = Colls.map(relationService.followings(cuid), f ->
        new FollowInfoLite(f.getTarget().getId(), Colls.map(f.getTags(), Tag::getId)));
    FollowListLite list = new FollowListLite(null, cuid, "所有关注", follows);
    return listService.addFollowList(list, cuid);
  }
}
