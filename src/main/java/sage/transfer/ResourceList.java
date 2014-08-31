package sage.transfer;

import com.fasterxml.jackson.databind.JavaType;
import sage.entity.ResourceListEntity;
import sage.web.context.Json;

import java.util.ArrayList;
import java.util.List;

public class ResourceList extends AList {
  private List<ResourceInfo> list;

  ResourceList() {}

  public ResourceList(Long id, Long ownerId, String name, List<ResourceInfo> list) {
    super(id, ownerId, name);
    this.list = list;
  }

  public List<ResourceInfo> getList() {
    return list;
  }

  public ResourceListEntity toEntity() {
    return new ResourceListEntity(getOwnerId(), getName(), Json.json(getList()));
  }

  public static ResourceList fromEntity(ResourceListEntity entity) {
    return new ResourceList(entity.getId(), entity.getOwnerId(), entity.getName(), Json.object(entity.getListJson(), RI_LIST));
  }

  private static JavaType RI_LIST = Json.typeFactory().constructCollectionType(ArrayList.class, ResourceInfo.class);
}
