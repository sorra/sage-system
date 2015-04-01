package sage.transfer;

import java.util.ArrayList;
import java.util.Collection;


public class FollowInfoLite {
  private Long userId;
  private Collection<Long> tagIds;
  
  FollowInfoLite() {}

  FollowInfoLite(FollowInfo followInfo) {
    userId = followInfo.getUser().getId();
    tagIds = new ArrayList<>();
    followInfo.getTags().forEach(tag -> tagIds.add(tag.getId()));
  }
  
  public Long getUserId() {
    return userId;
  }
  
  public Collection<Long> getTagIds() {
    return tagIds;
  }
}
