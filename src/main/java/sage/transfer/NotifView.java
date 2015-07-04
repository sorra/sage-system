package sage.transfer;

import java.util.Date;

import sage.entity.Notif;

public class NotifView {
  public long id;
  public long ownerId;
  public UserLabel sender;
  public Date time;
  public String type;
  public String desc;
  public String source;

  NotifView() {}

  public NotifView(Notif notif, UserLabel sender, String source) {
    id = notif.getId();
    ownerId = notif.getOwnerId();
    this.sender = sender;
    time = notif.getTime();
    type = notif.getType().name();
    desc = notif.getType().desc;
    this.source = source;
  }
}
