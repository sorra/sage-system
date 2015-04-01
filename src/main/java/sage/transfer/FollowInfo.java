package sage.transfer;

import java.util.Collection;

import sage.entity.Follow;

public class FollowInfo {
  private UserLabel user;
  private Collection<TagLabel> tags;
  
  FollowInfo() {}

  public FollowInfo(UserLabel user, Collection<TagLabel> tags) {
    this.user = user;
    this.tags = tags;
  }

  public FollowInfo(Follow follow) {
    user = new UserLabel(follow.getTarget());
    tags = TagLabel.listOf(follow.getTags());
  }
  
  public UserLabel getUser() {
    return user;
  }
  
  public Collection<TagLabel> getTags() {
    return tags;
  }
}
