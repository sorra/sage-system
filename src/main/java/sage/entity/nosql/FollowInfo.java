package sage.entity.nosql;

import java.util.Collection;

import sage.entity.Follow;
import sage.transfer.TagLabel;
import sage.transfer.UserLabel;

public class FollowInfo {
  private UserLabel target;
  private Collection<TagLabel> tags;
  
  FollowInfo() {}
  
  public FollowInfo(Follow follow) {
    target = new UserLabel(follow.getTarget());
    tags = TagLabel.listOf(follow.getTags());
  }
  
  public UserLabel getTarget() {
    return target;
  }
  
  public Collection<TagLabel> getTags() {
    return tags;
  }
}
