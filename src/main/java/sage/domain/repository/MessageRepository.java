package sage.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import sage.entity.Message;

@Repository
public class MessageRepository extends BaseRepository<Message> {

  public List<Message> byFrom(Long fromUser) {
    return session().createQuery("from Message m where m.fromUser=:fromUser")
        .setLong("fromUser", fromUser).list();
  }

  public  List<Message> byTo(Long toUser) {
    return session().createQuery("from Message m where m.toUser=:toUser")
        .setLong("toUser", toUser).list();
  }

  public List<Message> byFromTo(Long fromUser, Long toUser) {
    return session().createQuery("from Message m where m.fromUser=:fromUser and m.toUser=:toUser")
        .setLong("fromUser", fromUser).setLong("toUser", toUser).list();
  }

  @Override
  protected Class<Message> entityClass() {
    return Message.class;
  }
}
