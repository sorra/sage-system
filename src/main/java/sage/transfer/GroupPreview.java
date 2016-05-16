package sage.transfer;

import sage.entity.Group;
import sage.util.Colls;

import java.util.Collection;
import java.util.Date;

public class GroupPreview {
  public long id;
  public String name;
  public String introduction;
  public Collection<TagLabel> tags;
  public UserLabel creator;
  public Date createdTime;
  public int membersCount;

  GroupPreview() {}

  public GroupPreview(Group group) {
    id = group.getId();
    name = group.getName();
    introduction = group.getIntroduction();
    tags = Colls.map(group.getTags(), TagLabel::new);
    creator = new UserLabel(group.getCreator());
    createdTime = group.getWhenCreated();
    membersCount = group.getMembers().size();
  }
}
