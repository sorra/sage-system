package sage.domain.repository;

import org.springframework.stereotype.Repository;
import sage.entity.FollowListEntity;

@Repository
public class FollowListRepository extends BaseRepository<FollowListEntity> {

  @Override
  protected Class<FollowListEntity> entityClass() {
    return FollowListEntity.class;
  }
  
}
