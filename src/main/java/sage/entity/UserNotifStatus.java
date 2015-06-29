package sage.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserNotifStatus {
  private Long userId;
  private Long readToId;

  UserNotifStatus() {}

  public UserNotifStatus(Long userId, Long readToId) {
    this.userId = userId;
    this.readToId = readToId;
  }

  @Id
  public Long getUserId() {
    return userId;
  }
  void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getReadToId() {
    return readToId;
  }
  public void setReadToId(Long readToId) {
    this.readToId = readToId;
  }
}
