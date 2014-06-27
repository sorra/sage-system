package sage.domain.service;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sage.domain.repository.TagRepository;
import sage.entity.Tag;
import sage.transfer.TagCard;
import sage.transfer.TagLabel;
import sage.transfer.TagNode;
import sage.web.context.JsonUtil;

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
  public TagCard getTagCard(long tagId) {
    Tag tag = tagRepo.get(tagId);
    return tag == null ? null : new TagCard(tag);
  }

  @Transactional(readOnly = true)
  public TagLabel getTagLabel(long tagId) {
    Tag tag = tagRepo.get(tagId);
    return tag == null ? null : new TagLabel(tag);
  }

  @Transactional(readOnly = true)
  public TagNode getTagTree() {
    return new TagNode(tagRepo.get(Tag.ROOT_ID));
  }

  // TODO Cache it
  public String getTagTreeJson() {
    return JsonUtil.json(getTagTree());
  }

  @Transactional(readOnly = true)
  public Collection<Tag> getQueryTags(long tagId) {
    return TagRepository.getQueryTags(tagRepo.get(tagId));
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
