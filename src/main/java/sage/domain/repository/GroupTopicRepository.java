package sage.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import sage.entity.GroupTopic;

@Repository
public class GroupTopicRepository extends BaseRepository<GroupTopic> {

  public List<GroupTopic> byGroup(long groupId) {
    return session().createQuery("from GroupTopic gt where gt.group.id = :groupId and gt.hidden = false")
        .setLong("groupId", groupId).list();
  }

  @Override
  protected Class<GroupTopic> entityClass() {
    return null;
  }
}
