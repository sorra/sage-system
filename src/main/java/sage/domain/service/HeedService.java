package sage.domain.service;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import sage.domain.repository.HeededTagRepository;
import sage.domain.repository.TagRepository;
import sage.domain.repository.UserRepository;
import sage.entity.HeededTag;

@Service
@Transactional
public class HeedService {
  @Autowired
  private HeededTagRepository heededTagRepo;
  @Autowired
  private TagRepository tagRepo;
  @Autowired
  private UserRepository userRepo;
  
  public Collection<HeededTag> heededTags(long userId) {
    return new ArrayList<>(heededTagRepo.findByUser(userId));
  }
  
  public void heedTag(long userId, long tagId) {
    heededTagRepo.merge(new HeededTag(userRepo.load(userId), tagRepo.load(tagId)));
  }
  
  public void unheedTag(long userId, long tagId) {
    HeededTag ht = heededTagRepo.find(userId, tagId);
    Assert.notNull(ht);
    heededTagRepo.delete(ht);
  }
}
