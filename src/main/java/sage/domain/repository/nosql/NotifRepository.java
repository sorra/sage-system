package sage.domain.repository.nosql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import sage.entity.nosql.Notif;
import sage.web.context.JsonUtil;

import com.couchbase.client.protocol.views.Query;
import com.couchbase.client.protocol.views.ViewResponse;
import com.couchbase.client.protocol.views.ViewRow;

@Repository
public class NotifRepository extends BaseCouchbaseRepository<Notif> {

  public Collection<Notif> findAll(Long ownerId) {
    ViewResponse resp = client.query(client.getView("dev_Notif", "by_owner_id"),
        new Query().setIncludeDocs(true).setKey(ownerId.toString()));
    
    List<Notif> notifs = new ArrayList<>();
    for (ViewRow row : resp) {
      Notif notif = JsonUtil.object(row.getDocument().toString(), entityClass());
      notifs.add(notif);
    }
    return notifs;
  }
  
  @Override
  protected Class<Notif> entityClass() {
    return Notif.class;
  }

}
