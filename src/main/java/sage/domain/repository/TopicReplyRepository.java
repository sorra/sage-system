package sage.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import sage.entity.TopicReply;

@Repository
public class TopicReplyRepository extends BaseRepository<TopicReply> {

  public List<TopicReply> byTopicPost(long id) {
    return session().createQuery("from TopicReply r where r.topicPost.id = :id")
        .setLong("id", id).list();
  }

  @Override
  protected Class<TopicReply> entityClass() {
    return TopicReply.class;
  }
}
