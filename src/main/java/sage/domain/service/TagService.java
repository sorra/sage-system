package sage.domain.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import static java.util.Optional.ofNullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sage.domain.repository.TagRepository;
import sage.entity.Tag;
import sage.transfer.TagCard;
import sage.transfer.TagLabel;
import sage.transfer.TagNode;
import sage.web.context.Json;

@Service
@Transactional
public class TagService {
  @Autowired
  private TagRepository tagRepo;

  public long newTag(String name, long parentId) {
    Tag tag = new Tag(name, tagRepo.load(parentId));
    if (tagRepo.byNameAndParent(name, parentId) == null) {
      tagRepo.save(tag);
      return tag.getId();
    }
    else
      return -1;
  }

  @Transactional(readOnly = true)
  public Optional<TagCard> getTagCard(long tagId) {
    return ofNullable(tagRepo.get(tagId)).map(TagCard::new);
  }

  @Transactional(readOnly = true)
  public Optional<Tag> getTag(long tagId) {
    return ofNullable(tagRepo.get(tagId));
  }

  @Transactional(readOnly = true)
  public Optional<TagLabel> getTagLabel(long tagId) {
    return ofNullable(tagRepo.get(tagId)).map(TagLabel::new);
  }

  @Transactional(readOnly = true)
  public TagNode getTagTree() {
    return new TagNode(tagRepo.get(Tag.ROOT_ID));
  }

  // TODO Cache it
  public String getTagTreeJson() {
    return Json.json(getTagTree());
  }

  @Transactional(readOnly = true)
  public Collection<Tag> getQueryTags(long tagId) {
    return ofNullable(tagRepo.get(tagId)).map(TagRepository::getQueryTags).orElse(Collections.emptySet());
  }

  @Transactional(readOnly = true)
  public Collection<Tag> getTagsByName(String name) {
    return new ArrayList<>(tagRepo.byName(name));
  }
  
  @Transactional(readOnly = true)
  public Collection<Tag> getSameNameTags(long tagId) {
    Tag tag = tagRepo.get(tagId);
    Collection<Tag> tagsByName = getTagsByName(tag.getName());
    tagsByName.remove(tag);
    return tagsByName;
  }

  public void setIntro(long id, String intro) {
    tagRepo.load(id).setIntro(intro);
  }

  public void changeParent(long id, long parentId) {
    tagRepo.get(id).setParent(tagRepo.load(parentId));
  }

  public void init() {
    if (needInitialize) {
      tagRepo.save(new Tag(Tag.ROOT_NAME, null));
      needInitialize = false;
    }
  }

  private static boolean needInitialize = true;
}
