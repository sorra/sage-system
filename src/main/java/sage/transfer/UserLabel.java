package sage.transfer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import sage.entity.User;

public class UserLabel {
  private long id;
  private String name;
  private String avatar;
  private String intro;

  UserLabel() {}
  
  public UserLabel(User user) {
    this(user.getId(), user.getName(), user.getAvatar(), user.getIntro());
  }

  public UserLabel(long _id, String _name, String _avatar, String _intro) {
    id = _id;
    name = _name;
    avatar = _avatar;
    intro = _intro;
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

  public String getIntro() {
    return intro;
  }

  @Override
  public String toString() {
    return String.format("UserLabel[%d, %s]", id, name);
  }
}
