package sage.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import sage.domain.service.ListService;
import sage.domain.service.RelationService;
import sage.transfer.FollowList;
import sage.transfer.FollowListLite;
import sage.transfer.ResourceList;
import sage.web.auth.Auth;
import sage.web.context.Json;

@RestController
@RequestMapping("/list")
public class ListController {
  @Autowired
  private ListService listService;
  @Autowired
  RelationService rs;
  
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
  public Boolean updateFollowList(@PathVariable String id, @RequestParam String listLite) {
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
    Assert.isTrue(fcLite.getOwnerId().equals(uid));
    return listService.addFollowList(fcLite, uid);
  }
}
