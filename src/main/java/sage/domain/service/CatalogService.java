package sage.domain.service;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sage.domain.repository.FollowRepository;
import sage.domain.repository.nosql.BaseCouchbaseRepository;
import sage.domain.repository.nosql.FollowCatalogRepository;
import sage.domain.repository.nosql.ResourceCatalogRepository;
import sage.entity.nosql.Catalog;
import sage.entity.nosql.FollowCatalog;
import sage.entity.nosql.FollowCatalogLite;
import sage.entity.nosql.FollowInfo;
import sage.entity.nosql.FollowInfoLite;
import sage.entity.nosql.ResourceCatalog;

@Service
public class CatalogService {
  @Autowired
  private ResourceCatalogRepository resourceCatalogRepo;
  @Autowired
  private FollowCatalogRepository followCatalogRepo;
  @Autowired
  private FollowRepository followRepo;
  
  public ResourceCatalog getResourceCatalog(String key) {
    return resourceCatalogRepo.get(key);
  }
  
  public Boolean addResourceCatalog(ResourceCatalog rc, Long ownerId) {
    return addCatalog(rc, ownerId, resourceCatalogRepo);
  }
  
  public Boolean updateResourceCatalog(ResourceCatalog rc) {
    try {
      return resourceCatalogRepo.set(rc.getId(), rc).get();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }
  
  public FollowCatalog getFollowCatalog(String key) {
    return followCatalogRepo.get(key);
  }
  
  public Boolean addFollowCatalog(FollowCatalogLite fcLite, Long ownerId) {
    return addCatalog(toFollowCatalog(fcLite), ownerId, followCatalogRepo);
  }
  
  public Boolean updateFollowCatalog(FollowCatalogLite fcLite) {
    FollowCatalog fc = toFollowCatalog(fcLite);
    try {
      return followCatalogRepo.set(fc.getId(), fc).get();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }
  
  private FollowCatalog toFollowCatalog(FollowCatalogLite fcl) {
    final Long ownerId = fcl.getOwnerId();
    FollowCatalog fc = new FollowCatalog(ownerId, fcl.getName());
    for (FollowInfoLite followInfoLite : fcl.getList()) {
      Long targetId = followInfoLite.getUserId();
      FollowInfo followInfo = new FollowInfo(followRepo.find(ownerId, targetId));
      fc.getList().add(followInfo);
    }
    return fc;
  }

  private <T extends Catalog> Boolean addCatalog(Catalog catalog, Long ownerId, BaseCouchbaseRepository<T> repo) {
    catalog.setOwnerId(ownerId);
    long time = System.currentTimeMillis();
    String id = generateId(catalog, time);
    try {
      Boolean success = repo.add(id, (T) catalog).get();
      if (success) {
        return success;
      } else {
        // Retry once
        id = generateId(catalog, time+1);
        return repo.add(id, (T) catalog).get();
      }
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }
  
  private String generateId(Catalog rc, long time) {
    String id = rc.getOwnerId() + "_" + Long.toHexString(time);
    rc.setId(id);
    return id;
  }
}
