package sage.domain.repository;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Repository;

import sage.entity.Tag;

@Repository
public class TagRepository extends BaseRepository<Tag> {
  public Tag byNameAndParent(String name, long parentId) {
    for (Tag child : get(parentId).getChildren()) {
      if (child.getName().equals(name)) {
        return child;
      }
    }
    return null;
  }

  public Collection<Tag> byName(String name) {
    return session().createQuery("from Tag t where t.name = :name")
        .setString("name", name)
        .list();
  }

  public Set<Tag> byIds(Collection<Long> ids) {
    Set<Tag> tags = new HashSet<>();
    for (long id : ids) {
      tags.add(get(id));
    }
    return tags;
  }

  public static Set<Tag> getQueryTags(Tag tag) {
    Set<Tag> queryTags = new HashSet<>(tag.descendants());
    queryTags.add(tag);
    return queryTags;
  }

  public static Set<Tag> getQueryTags(Collection<Tag> tags) {
    Set<Tag> queryTags = new HashSet<>();
    for (Tag node : tags) {
      queryTags.add(node);
      queryTags.addAll(node.descendants());
    }
    return queryTags;
  }

  @Override
  protected Class<Tag> entityClass() {
    return Tag.class;
  }

  // public boolean noMatch(Collection<Tag> entityTags, Collection<Tag>
  // queryTags) {
  // for (Tag queryTag : queryTags) {
  // if (entityTags.contains(queryTag)) {
  // return false;
  // }
  // }
  // return true;
  // }

}
