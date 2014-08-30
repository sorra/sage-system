package sage.domain.service;

import httl.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import sage.domain.repository.*;
import sage.entity.FollowCatalogEntity;
import sage.entity.ResourceCatalogEntity;
import sage.transfer.*;

import java.util.ArrayList;

@Service
@Transactional
public class CatalogService {
  @Autowired
  private ResourceCatalogRepository resourceCatalogRepo;
  @Autowired
  private FollowCatalogRepository followCatalogRepo;
  @Autowired
  private FollowRepository followRepo;
  @Autowired
  private UserRepository userRepo;
  @Autowired
  private TagRepository tagRepo;
  
  public ResourceCatalog getResourceCatalog(Long id) {
    return ResourceCatalog.fromEntity(resourceCatalogRepo.get(id));
  }
  
  public Long addResourceCatalog(ResourceCatalog rc, Long userId) {
    ResourceCatalogEntity entity = escaped(rc).toEntity();
    entity.setOwnerId(userId);
    resourceCatalogRepo.save(entity);
    return entity.getId();
  }
  
  public void updateResourceCatalog(ResourceCatalog rc, Long userId) {
    ResourceCatalogEntity entity = resourceCatalogRepo.get(rc.getId());
    Assert.isTrue(entity.getOwnerId().equals(userId));

    ResourceCatalogEntity neo = escaped(rc).toEntity();
    entity.setName(neo.getName());
    entity.setListJson(neo.getListJson());
    resourceCatalogRepo.update(entity);
  }
  
  public FollowCatalog getFollowCatalog(Long id) {
    return FollowCatalogLite.fromEntity(followCatalogRepo.get(id))
        .toFull($ -> new UserLabel(userRepo.get($)), $ -> new TagLabel(tagRepo.get($)));
  }
  
  public Long addFollowCatalog(FollowCatalogLite fcLite, Long ownerId) {
    FollowCatalogEntity entity = fcLite.toEntity();
    entity.setOwnerId(ownerId);
    followCatalogRepo.save(entity);
    return entity.getId();
  }
  
  public void updateFollowCatalog(FollowCatalogLite fcLite, Long userId) {
    FollowCatalogEntity entity = followCatalogRepo.get(fcLite.getId());
    Assert.isTrue(entity.getOwnerId().equals(userId));

    FollowCatalogEntity neo = fcLite.toEntity();
    entity.setName(neo.getName());
    entity.setListJson(neo.getListJson());
    followCatalogRepo.update(entity);
  }

  private ResourceCatalog escaped(ResourceCatalog rc) {
    ResourceCatalog neo = new ResourceCatalog(rc.getId(), rc.getOwnerId(), rc.getName(), new ArrayList<>());
    rc.getList().forEach(info -> neo.getList().add(
        new ResourceInfo(StringUtils.escapeXml(info.getLink()), StringUtils.escapeXml(info.getDesc()))));
    return neo;
  }
}
