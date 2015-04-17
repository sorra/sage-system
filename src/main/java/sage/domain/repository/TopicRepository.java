package sage.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import sage.entity.Topic;

@Repository
public class TopicRepository extends BaseRepository<Topic> {

  public List<Topic> all() {
    return session().createQuery("from Topic t").list();
  }

  @Override
  protected Class<Topic> entityClass() {
    return Topic.class;
  }
}
