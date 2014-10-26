package sage.domain.service;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import sage.domain.repository.TagHeedRepository;
import sage.domain.repository.TagRepository;
import sage.domain.repository.UserRepository;
import sage.entity.TagHeed;

@Service
@Transactional
public class HeedService {
  @Autowired
  private TagHeedRepository tagHeedRepo;
  @Autowired
  private TagRepository tagRepo;
  @Autowired
  private UserRepository userRepo;
  
  public Collection<TagHeed> tagHeeds(long userId) {
    return new ArrayList<>(tagHeedRepo.findByUser(userId));
  }
  
  public void heedTag(long userId, long tagId) {
    tagHeedRepo.merge(new TagHeed(userRepo.load(userId), tagRepo.load(tagId)));
  }
  
  public void unheedTag(long userId, long tagId) {
    TagHeed ht = tagHeedRepo.find(userId, tagId);
    Assert.notNull(ht);
    tagHeedRepo.delete(ht);
  }
}
