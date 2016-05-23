package sage.transfer;

import java.util.Date;

import sage.entity.Notification;

public class NotifView {
  public long id;
  public long ownerId;
  public UserLabel sender;
  public Date time;
  public String type;
  public String desc;
  public String source;
  public boolean read = false;

  NotifView() {}

  public NotifView(Notification notification, UserLabel sender, String source) {
    id = notification.getId();
    ownerId = notification.getOwnerId();
    this.sender = sender;
    time = notification.getWhenCreated();
    type = notification.getType().name();
    desc = notification.getType().getDesc();
    this.source = source;
  }
}
