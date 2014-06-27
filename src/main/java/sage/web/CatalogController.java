package sage.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sage.domain.service.CatalogService;
import sage.domain.service.RelationService;
import sage.entity.nosql.FollowCatalog;
import sage.entity.nosql.FollowCatalogLite;
import sage.entity.nosql.ResourceCatalog;
import sage.web.auth.AuthUtil;
import sage.web.context.JsonUtil;

@Controller
@RequestMapping("/catalog")
public class CatalogController {
  @Autowired
  private CatalogService catalogs;
  @Autowired
  RelationService rs;
  
  @RequestMapping(value = "/resource/{id}", method = RequestMethod.GET)
  @ResponseBody
  public ResourceCatalog getResourceCatalog(@PathVariable("id") String id) {
    AuthUtil.checkCurrentUid();
    return catalogs.getResourceCatalog(id);
  }
  
  @RequestMapping(value = "/resource/{id}", method = RequestMethod.POST)
  @ResponseBody
  public Boolean updateResourceCatalog(@PathVariable("id") String id, @RequestParam("catalog") String catalog) {
    Long uid = AuthUtil.checkCurrentUid();
    
    ResourceCatalog rc = JsonUtil.object(catalog, ResourceCatalog.class);
    Assert.isTrue(rc.getOwnerId().equals(uid));
    Assert.isTrue(rc.getId().equals(id));
    return catalogs.updateResourceCatalog(rc);
  }

  @RequestMapping(value = "/resource/add", method = RequestMethod.POST)
  @ResponseBody
  public String addResourceCatalog(@RequestParam("catalog") String catalog) {
    Long uid = AuthUtil.checkCurrentUid();
    
    ResourceCatalog rc = JsonUtil.object(catalog, ResourceCatalog.class);
    return catalogs.addResourceCatalog(rc, uid) ? rc.getId() : null;
  }
  
  @RequestMapping(value = "/follow/{id}", method = RequestMethod.GET)
  @ResponseBody
  public FollowCatalog getFollowCatalog(@PathVariable("id") String id) {
    AuthUtil.checkCurrentUid();
    return catalogs.getFollowCatalog(id);
  }
  
  @RequestMapping(value = "/follow/{id}", method = RequestMethod.POST)
  @ResponseBody
  public Boolean updateFollowCatalog(@PathVariable("id") String id, @RequestParam("catalogLite") String catalogLite) {
    Long uid = AuthUtil.checkCurrentUid();
    
    FollowCatalogLite fcLite = JsonUtil.object(catalogLite, FollowCatalogLite.class);
    Assert.isTrue(fcLite.getOwnerId().equals(uid));
    Assert.isTrue(fcLite.getId().equals(id));
    return catalogs.updateFollowCatalog(fcLite);
  }

  @RequestMapping(value = "/follow/add", method = RequestMethod.POST)
  @ResponseBody
  public String addFollowCatalog(@RequestParam("catalogLite") String catalogLite) {
    Long uid = AuthUtil.checkCurrentUid();

    FollowCatalogLite fcLite = JsonUtil.object(catalogLite, FollowCatalogLite.class);
    return catalogs.addFollowCatalog(fcLite, uid) ? fcLite.getId() : null;
  }
}
