package sage.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import sage.entity.TopicReply;

@Repository
public class TopicReplyRepository extends BaseRepository<TopicReply> {

  public List<TopicReply> byTopicPost(long id) {
    return session().createQuery("from TopicReply r where r.topicPost.id = :id")
        .setLong("id", id).list();
  }

  public Optional<TopicReply> theLastByTopicPost(long id) {
    Object res = session().createQuery("from TopicReply r where r.topicPost.id = :id order by r.id desc")
        .setLong("id", id).setMaxResults(1).uniqueResult();
    return Optional.ofNullable((TopicReply) res);
  }

  @Override
  protected Class<TopicReply> entityClass() {
    return TopicReply.class;
  }
}
