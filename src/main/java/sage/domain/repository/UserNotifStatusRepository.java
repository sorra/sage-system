package sage.domain.repository;

import org.springframework.stereotype.Repository;
import sage.entity.UserNotifStatus;

@Repository
public class UserNotifStatusRepository extends BaseRepository<UserNotifStatus> {
  @Override
  protected Class entityClass() {
    return UserNotifStatus.class;
  }
}
