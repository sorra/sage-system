package sage.entity.nosql;

import java.util.ArrayList;
import java.util.List;

/**
 * Stored in NoSQL
 */
public class FollowCatalog extends Catalog {
  private List<FollowInfo> list;
  
  FollowCatalog() {}
  
  public FollowCatalog(Long ownerId, String name) {
    super(ownerId, name);
    list = new ArrayList<>();
  }

  public List<FollowInfo> getList() {
    return list;
  }
}
