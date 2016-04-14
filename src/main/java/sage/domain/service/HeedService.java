package sage.domain.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import sage.domain.repository.*;
import sage.entity.FollowListHeed;
import sage.entity.TagHeed;
import sage.util.Colls;

@Service
@Transactional
public class HeedService {
  @Autowired
  private TagRepository tagRepo;
  @Autowired
  private UserRepository userRepo;
  @Autowired
  private TagHeedRepository tagHeedRepo;
  @Autowired
  private FollowListHeedRepository followListHeedRepo;
  @Autowired
  private FollowListRepository followListRepo;
  
  public Collection<TagHeed> tagHeeds(long userId) {
    return Colls.copy(tagHeedRepo.findByUser(userId));
  }
  
  public void heedTag(long userId, long tagId) {
    if (tagHeedRepo.find(userId, tagId) == null) {
      tagHeedRepo.save(new TagHeed(userRepo.load(userId), tagRepo.load(tagId)));
    }
  }
  
  public void unheedTag(long userId, long tagId) {
    TagHeed existing = tagHeedRepo.find(userId, tagId);
    Assert.notNull(existing);
    tagHeedRepo.delete(existing);
  }

  public Collection<FollowListHeed> followListHeeds(long userId) {
    return Colls.copy(followListHeedRepo.byUser(userId));
  }

  public boolean existsFollowListHeed(long userId, long followListId) {
    return followListHeedRepo.byUserAndList(userId, followListId) != null;
  }

  public void heedFollowList(long userId, long followListId) {
    if (followListHeedRepo.byUserAndList(userId, followListId) == null) {
      followListHeedRepo.save(new FollowListHeed(userId, followListRepo.load(followListId)));
    }
  }

  public void unheedFollowList(long userId, long followListId) {
    FollowListHeed heed = followListHeedRepo.byUserAndList(userId, followListId);
    if (heed != null) {
      followListHeedRepo.delete(heed);
    }
  }
}
