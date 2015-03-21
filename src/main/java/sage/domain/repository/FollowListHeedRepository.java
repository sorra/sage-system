package sage.domain.repository;

import java.util.Collection;

import org.springframework.stereotype.Repository;
import sage.entity.FollowListHeed;

@Repository
public class FollowListHeedRepository extends BaseRepository<FollowListHeed> {

  public Collection<FollowListHeed> byUser(long userId) {
    return session().createQuery("from FollowListHeed heed where heed.userId = :userId")
        .setParameter("userId", userId)
        .list();
  }

  public FollowListHeed byUserAndList(long userId, long followListId) {
    return (FollowListHeed) session().createQuery(
        "from FollowListHeed heed where heed.userId = :userId and heed.list.id = :followListId")
        .setParameter("userId", userId).setParameter("followListId", followListId)
        .uniqueResult();
  }

  @Override
  protected Class<FollowListHeed> entityClass() {
    return FollowListHeed.class;
  }
}
