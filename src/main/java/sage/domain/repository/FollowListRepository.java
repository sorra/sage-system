package sage.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import sage.entity.FollowListEntity;

@Repository
public class FollowListRepository extends BaseRepository<FollowListEntity> {

  public List<FollowListEntity> byOwner(long ownerId) {
    return session().createQuery("from FollowListEntity e where e.ownerId = :ownerId")
        .setLong("ownerId", ownerId).list();
  }

  @Override
  protected Class<FollowListEntity> entityClass() {
    return FollowListEntity.class;
  }
  
}
