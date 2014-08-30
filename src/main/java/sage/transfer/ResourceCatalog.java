package sage.transfer;

import com.fasterxml.jackson.databind.JavaType;
import sage.entity.ResourceCatalogEntity;
import sage.web.context.Json;

import java.util.ArrayList;
import java.util.List;

public class ResourceCatalog extends Catalog {
  private List<ResourceInfo> list;

  ResourceCatalog() {}

  public ResourceCatalog(Long id, Long ownerId, String name, List<ResourceInfo> list) {
    super(id, ownerId, name);
    this.list = list;
  }

  public List<ResourceInfo> getList() {
    return list;
  }

  public ResourceCatalogEntity toEntity() {
    return new ResourceCatalogEntity(getOwnerId(), getName(), Json.json(getList()));
  }

  public static ResourceCatalog fromEntity(ResourceCatalogEntity entity) {
    return new ResourceCatalog(entity.getId(), entity.getOwnerId(), entity.getName(), Json.object(entity.getListJson(), RI_LIST));
  }

  private static JavaType RI_LIST = Json.typeFactory().constructCollectionType(ArrayList.class, ResourceInfo.class);
}
