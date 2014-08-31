package sage.transfer;

import java.util.ArrayList;
import java.util.List;

public class FollowList extends AList {
  private List<FollowInfo> list;

  FollowList() {}

  public FollowList(Long id, Long ownerId, String name, List<FollowInfo> list) {
    super(id, ownerId, name);
    this.list = list;
  }

  public List<FollowInfo> getList() {
    return list;
  }

  public FollowListLite toLite() {
    List<FollowInfoLite> liteList = new ArrayList<>();
    for (FollowInfo each : getList()) {
      liteList.add(new FollowInfoLite(each));
    }

    return new FollowListLite(getId(), getOwnerId(), getName(), liteList);
  }
}
