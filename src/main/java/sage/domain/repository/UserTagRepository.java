package sage.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import sage.entity.UserTag;

@Repository
public class UserTagRepository extends BaseRepository<UserTag> {

  public UserTag find(long userId, long tagId) {
    return (UserTag) session().createQuery("from UserTag ut where ut.userId = :userId and ut.tagId = :tagId")
        .setLong("userId", userId).setLong("tagId", tagId).uniqueResult();
  }

  /** @return 0 for null */
  public long latestIdByUser(long userId) {
    Long id = (Long) session().createQuery(
        "select ut.id from UserTag ut where ut.userId = :userId order by ut.id desc")
        .setLong("userId", userId).setMaxResults(1).uniqueResult();
    return id != null ? id : 0;
  }

  public List<UserTag> byUser(long userId) {
    return session().createQuery("from UserTag ut where ut.userId = :userId")
        .setLong("userId", userId).list();
  }

  public List<UserTag> byUserAndAfterId(long userId, Long id) {
    if (id == null) id = 0L;
    return session().createQuery("from UserTag ut where ut.userId = :userId and ut.id > :id")
        .setLong("userId", userId).setLong("id", id).list();
  }

  @Override
  protected Class<UserTag> entityClass() {
    return UserTag.class;
  }
}
