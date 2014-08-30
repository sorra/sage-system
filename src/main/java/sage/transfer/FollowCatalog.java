package sage.transfer;

import sage.entity.FollowCatalogEntity;
import sage.web.context.Json;

import java.util.ArrayList;
import java.util.List;

public class FollowCatalog extends Catalog {
  private List<FollowInfo> list;

  FollowCatalog() {}

  public FollowCatalog(Long id, Long ownerId, String name, List<FollowInfo> list) {
    super(id, ownerId, name);
    this.list = list;
  }

  public List<FollowInfo> getList() {
    return list;
  }

  public FollowCatalogLite toLite() {
    List<FollowInfoLite> liteList = new ArrayList<>();
    for (FollowInfo each : getList()) {
      liteList.add(new FollowInfoLite(each));
    }

    return new FollowCatalogLite(getId(), getOwnerId(), getName(), liteList);
  }
}
