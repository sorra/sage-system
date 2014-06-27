package sage.entity.nosql;

import java.util.ArrayList;
import java.util.List;

/**
 * Stored in NoSQL
 */
public class ResourceCatalog extends Catalog {
  private List<ResourceInfo> list;
  
  ResourceCatalog() {}
  
  public ResourceCatalog(Long ownerId, String name) {
    super(ownerId, name);
    list = new ArrayList<>();
  }
  
  public List<ResourceInfo> getList() {
    return list;
  }
}
