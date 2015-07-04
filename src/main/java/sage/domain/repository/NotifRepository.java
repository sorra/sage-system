package sage.domain.repository;

import java.util.Collection;

import org.springframework.stereotype.Repository;
import sage.entity.Notif;

@Repository
public class NotifRepository extends BaseRepository<Notif> {

  public Collection<Notif> byOwner(long ownerId) {
    return session().createQuery("from Notif n where n.ownerId = :ownerId" + ORDER)
        .setLong("ownerId", ownerId).list();
  }

  public Collection<Notif> byOwnerAndAfterId(long ownerId, long notifId) {
    return session().createQuery("from Notif n where n.ownerId = :ownerId and id > :notifId" + ORDER)
        .setLong("ownerId", ownerId).setLong("notifId", notifId).list();
  }

  private static String ORDER = " order by n.id desc";
  
  @Override
  protected Class<Notif> entityClass() {
    return Notif.class;
  }

}
