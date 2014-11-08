package sage.domain.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import sage.domain.repository.TagRepository;
import sage.entity.Tag;
import sage.transfer.TagCard;
import sage.transfer.TagLabel;
import sage.transfer.TagNode;
import sage.web.context.Json;

@Service
@Transactional(readOnly = true)
public class TagService {
  @Autowired
  private TagRepository tagRepo;

  @Transactional(readOnly = false)
  public Long newTag(String name, long parentId) {
    Tag tag = new Tag(name, tagRepo.load(parentId));
    if (tagRepo.byNameAndParent(name, parentId) == null) {
      tagRepo.save(tag);
      return tag.getId();
    }
    else
      return null;
  }

  @Transactional(readOnly = false)
  public void setIntro(long id, String intro) {
    tagRepo.load(id).setIntro(intro);
  }

  @Transactional(readOnly = false)
  public void changeParent(long id, long parentId) {
    tagRepo.get(id).setParent(tagRepo.load(parentId));
  }

  public Optional<TagCard> getTagCard(long tagId) {
    return tagRepo.optional(tagId).map(TagCard::new);
  }

  public Optional<Tag> getTag(long tagId) {
    return tagRepo.optional(tagId);
  }

  public Optional<TagLabel> getTagLabel(long tagId) {
    return tagRepo.optional(tagId).map(TagLabel::new);
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
    Assert.isTrue(
        tagRepo.save(new Tag(Tag.ROOT_NAME, null)).getId().equals(Tag.ROOT_ID));
    needInitialize = false;
  }

  private static volatile boolean needInitialize = true;
}
