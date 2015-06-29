package sage.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Notif {
  
  private Long id;
  private Long ownerId;
  private Long senderId;
  private Type type;
  private Long sourceId;
  private Date time;
  
  Notif() {}
  
  public Notif(Long ownerId, Long senderId, Type type, Long sourceId) {
    this.ownerId = ownerId;
    this.senderId = senderId;
    this.type = type;
    this.sourceId = sourceId;
    this.time = new Date();
  }

  @Id @GeneratedValue
  public Long getId() {
    return id;
  }
  void setId(Long id) {
    this.id = id;
  }
  
  public Long getOwnerId() {
    return ownerId;
  }
  void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }

  public Long getSenderId() {
    return senderId;
  }
  void setSenderId(Long senderId) {
    this.senderId = senderId;
  }

  public Type getType() {
    return type;
  }
  void setType(Type type) {
    this.type = type;
  }

  public Long getSourceId() {
    return sourceId;
  }
  void setSourceId(Long sourceId) {
    this.sourceId = sourceId;
  }

  public Date getTime() {
    return time;
  }
  public void setTime(Date time) {
    this.time = time;
  }

  public enum Type {
    FORWARDED, COMMENTED, REPLIED, MENTIONED_TWEET, MENTIONED_COMMENT, FOLLOWED
  }
}
