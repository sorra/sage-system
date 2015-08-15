package sage.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import sage.entity.TopicPost;

@Repository
public class TopicPostRepository extends BaseRepository<TopicPost> {

  public List<TopicPost> byGroup(long groupId) {
    return session().createQuery("from TopicPost gt where gt.group.id = :groupId and gt.hidden = false")
        .setLong("groupId", groupId).list();
  }

  @Override
  protected Class<TopicPost> entityClass() {
    return TopicPost.class;
  }
}
