package sage.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import sage.domain.service.CatalogService;
import sage.domain.service.RelationService;
import sage.entity.nosql.FollowCatalog;
import sage.entity.nosql.FollowCatalogLite;
import sage.entity.nosql.ResourceCatalog;
import sage.web.auth.Auth;
import sage.web.context.Json;

@RestController
@RequestMapping("/catalog")
public class CatalogController {
  @Autowired
  private CatalogService catalogs;
  @Autowired
  RelationService rs;
  
  @RequestMapping(value = "/resource/{id}", method = RequestMethod.GET)
  public ResourceCatalog getResourceCatalog(@PathVariable String id) {
    Auth.checkCurrentUid();
    return catalogs.getResourceCatalog(id);
  }
  
  @RequestMapping(value = "/resource/{id}", method = RequestMethod.POST)
  public Boolean updateResourceCatalog(@PathVariable String id, @RequestParam String catalog) {
    Long uid = Auth.checkCurrentUid();
    
    ResourceCatalog rc = Json.object(catalog, ResourceCatalog.class);
    Assert.isTrue(rc.getOwnerId().equals(uid));
    Assert.isTrue(rc.getId().equals(id));
    return catalogs.updateResourceCatalog(rc);
  }

  @RequestMapping(value = "/resource/add", method = RequestMethod.POST)
  public String addResourceCatalog(@RequestParam String catalog) {
    Long uid = Auth.checkCurrentUid();
    
    ResourceCatalog rc = Json.object(catalog, ResourceCatalog.class);
    return catalogs.addResourceCatalog(rc, uid) ? rc.getId() : null;
  }
  
  @RequestMapping(value = "/follow/{id}", method = RequestMethod.GET)
  public FollowCatalog getFollowCatalog(@PathVariable String id) {
    Auth.checkCurrentUid();
    return catalogs.getFollowCatalog(id);
  }
  
  @RequestMapping(value = "/follow/{id}", method = RequestMethod.POST)
  public Boolean updateFollowCatalog(@PathVariable String id, @RequestParam String catalogLite) {
    Long uid = Auth.checkCurrentUid();
    
    FollowCatalogLite fcLite = Json.object(catalogLite, FollowCatalogLite.class);
    Assert.isTrue(fcLite.getOwnerId().equals(uid));
    Assert.isTrue(fcLite.getId().equals(id));
    return catalogs.updateFollowCatalog(fcLite);
  }

  @RequestMapping(value = "/follow/add", method = RequestMethod.POST)
  public String addFollowCatalog(@RequestParam String catalogLite) {
    Long uid = Auth.checkCurrentUid();

    FollowCatalogLite fcLite = Json.object(catalogLite, FollowCatalogLite.class);
    return catalogs.addFollowCatalog(fcLite, uid) ? fcLite.getId() : null;
  }
}
