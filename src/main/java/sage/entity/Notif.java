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
    FOLLOWED(SourceType.USER, "关注了你", "新粉丝"),
    FORWARDED(SourceType.TWEET, "转发了你的微博", "转发"),
    COMMENTED(SourceType.COMMENT, "评论了你的微博", "评论"),
    REPLIED(SourceType.COMMENT, "回复了你", "回复"),
    MENTIONED_TWEET(SourceType.TWEET, "在微博中提到了你", "微博@"),
    MENTIONED_COMMENT(SourceType.COMMENT, "在评论中提到了你", "评论@"),
    MENTIONED_TOPIC_POST(SourceType.TOPIC_POST, "在帖子中提到了你", "帖子@"),
    MENTIONED_TOPIC_REPLY(SourceType.TOPIC_REPLY, "在帖子中提到了你", "帖子@"),
    REPIED_IN_TOPIC(SourceType.TOPIC_REPLY, "在帖子中回复了你", "帖子回复");
    public final SourceType sourceType;
    public final String desc;
    public final String shortDesc;
    Type(SourceType sourceType, String desc, String shortDesc) {
      this.sourceType = sourceType;
      this.desc = desc;
      this.shortDesc = shortDesc;
    }
  }

  public enum SourceType {
    USER, TWEET, COMMENT, TOPIC_POST, TOPIC_REPLY
  }
}
