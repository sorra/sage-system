package sage.transfer;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JavaType;
import sage.entity.ResourceListEntity;
import sage.web.context.Json;

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
    return new ResourceListEntity(getOwnerId(), getName(), Json.INSTANCE.json(getList()));
  }

  public static ResourceList fromEntity(ResourceListEntity entity) {
    return new ResourceList(entity.getId(), entity.getOwnerId(), entity.getName(), Json.INSTANCE.object(entity.getListJson(), RI_LIST));
  }

  private static JavaType RI_LIST = Json.INSTANCE.typeFactory().constructCollectionType(ArrayList.class, ResourceInfo.class);
}
