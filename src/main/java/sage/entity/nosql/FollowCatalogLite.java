package sage.entity.nosql;

import java.util.List;

public class FollowCatalogLite extends Catalog {
  private List<FollowInfoLite> list;
  
  FollowCatalogLite() {}
  
  public List<FollowInfoLite> getList() {
    return list;
  }
}
