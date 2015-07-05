package sage.transfer;

import java.util.Collection;

import sage.entity.Follow;
import sage.entity.Tag;
import sage.util.Colls;

public class UserCardFollow {
  public String reason;
  public Collection<Long> tagIds;
  public boolean includeNew;
  public boolean includeAll;

  UserCardFollow() {}

  public UserCardFollow(Follow follow) {
    reason = follow.getReason();
    tagIds = Colls.map(follow.getTags(), Tag::getId);
    includeNew = follow.isIncludeNew();
    includeAll = follow.isIncludeAll();
  }
}
