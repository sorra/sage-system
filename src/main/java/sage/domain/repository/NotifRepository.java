package sage.domain.repository;

import java.util.Collection;

import org.springframework.stereotype.Repository;
import sage.entity.Notif;

@Repository
public class NotifRepository extends BaseRepository<Notif> {

  public Collection<Notif> byOwner(Long ownerId) {
    return session().createQuery("from Notif n where n.ownerId = :ownerId")
        .setLong("ownerId", ownerId).list();
  }
  
  @Override
  protected Class<Notif> entityClass() {
    return Notif.class;
  }

}
