package sage.entity.nosql;

import java.util.Collection;


public class FollowInfoLite {
  private Long userId;
  private Collection<Long> tagIds;
  
  FollowInfoLite() {}
  
  public Long getUserId() {
    return userId;
  }
  
  public Collection<Long> getTagIds() {
    return tagIds;
  }
}
