package sage.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import sage.entity.TopicPost;

@Repository
public class TopicPostRepository extends BaseRepository<TopicPost> {

  public List<TopicPost> byGroup(long groupId) {
    return session().createQuery("from TopicPost tp where tp.group.id = :groupId and tp.hidden = false")
        .setLong("groupId", groupId).list();
  }

  public List<TopicPost> recent(int maxSize) {
    return session().createQuery("from TopicPost tp order by tp.time desc").setMaxResults(maxSize).list();
  }

  @Override
  protected Class<TopicPost> entityClass() {
    return TopicPost.class;
  }
}
