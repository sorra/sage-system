package sage.domain.service;

import java.util.ArrayList;

import httl.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import sage.domain.repository.*;
import sage.entity.FollowListEntity;
import sage.entity.ResourceListEntity;
import sage.transfer.*;

@Service
@Transactional
public class ListService {
  @Autowired
  private ResourceListRepository resourceListRepo;
  @Autowired
  private FollowListRepository followListRepo;
  @Autowired
  private FollowRepository followRepo;
  @Autowired
  private UserRepository userRepo;
  @Autowired
  private TagRepository tagRepo;
  
  public ResourceList getResourceList(Long id) {
    return ResourceList.fromEntity(resourceListRepo.nonNull(id));
  }
  
  public Long addResourceList(ResourceList rc, Long userId) {
    ResourceListEntity entity = escaped(rc).toEntity();
    entity.setOwnerId(userId);
    resourceListRepo.save(entity);
    return entity.getId();
  }
  
  public void updateResourceList(ResourceList rc, Long userId) {
    ResourceListEntity entity = resourceListRepo.nonNull(rc.getId());
    Assert.isTrue(entity.getOwnerId().equals(userId));

    ResourceListEntity neo = escaped(rc).toEntity();
    entity.setName(neo.getName());
    entity.setListJson(neo.getListJson());
    resourceListRepo.update(entity);
  }
  
  public FollowList getFollowList(Long id) {
    return FollowListLite.fromEntity(followListRepo.nonNull(id))
        .toFull($ -> new UserLabel(userRepo.nonNull($)), $ -> new TagLabel(tagRepo.nonNull($)));
  }
  
  public Long addFollowList(FollowListLite fcLite, Long ownerId) {
    FollowListEntity entity = fcLite.toEntity();
    entity.setOwnerId(ownerId);
    followListRepo.save(entity);
    return entity.getId();
  }
  
  public void updateFollowList(FollowListLite fcLite, Long userId) {
    FollowListEntity entity = followListRepo.nonNull(fcLite.getId());
    Assert.isTrue(entity.getOwnerId().equals(userId));

    FollowListEntity neo = fcLite.toEntity();
    entity.setName(neo.getName());
    entity.setListJson(neo.getListJson());
    followListRepo.update(entity);
  }

  private ResourceList escaped(ResourceList rc) {
    ResourceList neo = new ResourceList(rc.getId(), rc.getOwnerId(), rc.getName(), new ArrayList<>());
    rc.getList().forEach(info -> neo.getList().add(
        new ResourceInfo(StringUtils.escapeXml(info.getLink()), StringUtils.escapeXml(info.getDesc()))));
    return neo;
  }
}
