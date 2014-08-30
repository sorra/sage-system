package sage.transfer;

import com.fasterxml.jackson.databind.JavaType;
import sage.entity.FollowCatalogEntity;
import sage.web.context.Json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class FollowCatalogLite extends Catalog {
  private List<FollowInfoLite> list;
  
  FollowCatalogLite() {}

  public FollowCatalogLite(Long id, Long ownerId, String name, List<FollowInfoLite> list) {
    super(id, ownerId, name);
    this.list = list;
  }

  public List<FollowInfoLite> getList() {
    return list;
  }

  public FollowCatalog toFull(Function<Long, UserLabel> getUser, Function<Long, TagLabel> getTag) {
    List<FollowInfo> infoList = new ArrayList<>();
    for (FollowInfoLite infoLite : getList()) {
      List<TagLabel> tags = new ArrayList<>();
      for (Long tid : infoLite.getTagIds()) {tags.add(getTag.apply(tid));}
      infoList.add(new FollowInfo(getUser.apply(infoLite.getUserId()), tags));
    }

    return new FollowCatalog(getId(), getOwnerId(), getName(), infoList);
  }

  public FollowCatalogEntity toEntity() {
    return new FollowCatalogEntity(getOwnerId(), getName(), Json.json(getList()));
  }

  public static FollowCatalogLite fromEntity(FollowCatalogEntity entity) {
    return new FollowCatalogLite(entity.getId(), entity.getOwnerId(), entity.getName(), Json.object(entity.getListJson(), FIL_LIST));
  }

  private static JavaType FIL_LIST = Json.typeFactory().constructCollectionType(ArrayList.class, FollowInfoLite.class);
}
