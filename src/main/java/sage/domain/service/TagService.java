package sage.domain.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sage.domain.repository.TagRepository;
import sage.entity.Tag;
import sage.transfer.TagCard;
import sage.transfer.TagNode;
import sage.web.context.Json;

@Service
@Transactional(readOnly = true)
public class TagService {
  @Autowired
  private TagRepository tagRepo;

  public TagCard getTagCard(long tagId) {
    return new TagCard(tagRepo.get(tagId));
  }

  public Tag getTag(long tagId) {
    return tagRepo.get(tagId);
  }

  public TagNode getTagTree() {
    return new TagNode(tagRepo.get(Tag.ROOT_ID));
  }

  // TODO Cache it
  public String getTagTreeJson() {
    return Json.json(getTagTree());
  }

  public Collection<Tag> getQueryTags(long tagId) {
    return tagRepo.optional(tagId).map(TagRepository::getQueryTags).orElse(Collections.emptySet());
  }

  
  public Collection<Tag> getTagsByName(String name) {
    return new ArrayList<>(tagRepo.byName(name));
  }
  
  public Collection<Tag> getSameNameTags(long tagId) {
    Tag tag = tagRepo.get(tagId);
    Collection<Tag> tagsByName = getTagsByName(tag.getName());
    tagsByName.remove(tag);
    return tagsByName;
  }

  @Transactional(readOnly = false)
  public synchronized void init() {
    if (!needInitialize) {
      throw new RuntimeException();
    }
    assertEqual(tagRepo.save(new Tag(Tag.ROOT_NAME, null)).getId(), Tag.ROOT_ID);
    needInitialize = false;
  }

  private static volatile boolean needInitialize = true;

  private static void assertEqual(Object a, Object b) {
    boolean equal = Objects.equals(a, b);
    if (!equal) {
      throw new AssertionError("Not equal! a = "+a+", b = "+b);
    }
  }
}
