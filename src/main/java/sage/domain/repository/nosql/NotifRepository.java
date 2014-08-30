package sage.domain.repository.nosql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import sage.domain.repository.BaseRepository;
import sage.entity.Notif;
import sage.web.context.Json;

import com.couchbase.client.protocol.views.Query;
import com.couchbase.client.protocol.views.ViewResponse;
import com.couchbase.client.protocol.views.ViewRow;

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
