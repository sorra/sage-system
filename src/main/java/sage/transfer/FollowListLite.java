package sage.transfer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JavaType;
import sage.entity.FollowListEntity;
import sage.web.context.Json;

public class FollowListLite extends AList {
  private List<FollowInfoLite> list;
  
  FollowListLite() {}

  public FollowListLite(Long id, Long ownerId, String name, List<FollowInfoLite> list) {
    super(id, ownerId, name);
    this.list = list;
  }

  public List<FollowInfoLite> getList() {
    return list;
  }

  public FollowList toFull(Function<Long, UserLabel> getUser, Function<Long, TagLabel> getTag) {
    List<FollowInfo> infoList = new ArrayList<>();
    for (FollowInfoLite infoLite : getList()) {
      List<TagLabel> tags = new ArrayList<>();
      for (Long tid : infoLite.getTagIds()) {tags.add(getTag.apply(tid));}
      infoList.add(new FollowInfo(getUser.apply(infoLite.getUserId()), tags));
    }

    return new FollowList(getId(), getOwnerId(), getName(), infoList);
  }

  public FollowListEntity toEntity() {
    return new FollowListEntity(getOwnerId(), getName(), Json.json(getList()));
  }

  public static FollowListLite fromEntity(FollowListEntity entity) {
    return new FollowListLite(entity.getId(), entity.getOwnerId(), entity.getName(), Json.object(entity.getListJson(), FIL_LIST));
  }

  private static JavaType FIL_LIST = Json.typeFactory().constructCollectionType(ArrayList.class, FollowInfoLite.class);
}
