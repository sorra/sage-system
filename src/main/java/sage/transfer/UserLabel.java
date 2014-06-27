package sage.transfer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sage.entity.User;

public class UserLabel {
  private long id;
  private String name;
  private String avatar;

  UserLabel() {}
  
  public UserLabel(User user) {
    this(user.getId(), user.getName(), user.getAvatar());
  }

  public UserLabel(long _id, String _name, String _avatar) {
    id = _id;
    name = _name;
    avatar = _avatar;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getAvatar() {
    return avatar;
  }
  
  @Override
  public String toString() {
    return String.format("UserLabel[%d, %s]", id, name);
  }
  
  public static List<UserLabel> listOf(Collection<User> users) {
    List<UserLabel> labels = new ArrayList<>();
    for (User user : users) {
      labels.add(new UserLabel(user));
    }
    return labels;
  }
}
