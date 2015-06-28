package sage.domain.repository;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Repository;
import sage.entity.Group;
import sage.entity.Tag;

@Repository
public class GroupRepository extends BaseRepository<Group> {

  public List<Group> all() {
    return session().createQuery("from Group g").list();
  }

  public List<Group> byTags(Collection<Tag> tags) {
    if (tags.isEmpty()) {
      return new LinkedList<>();
    }
    return session().createQuery("select distinct g from Group g join g.tags tgs where tgs in :qtags")
        .setParameterList("qtags", TagRepository.getQueryTags(tags))
        .list();
  }

  @Override
  protected Class<Group> entityClass() {
    return Group.class;
  }
}
