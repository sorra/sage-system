package sage.entity.nosql;

public class Notif implements IdAble {
  
  private String id;
  private Long ownerId;
  private Long senderId;
  private Type type;
  private Long sourceId;
  
  Notif() {}
  
  public Notif(Long ownerId, Long senderId, Type type, Long sourceId) {
    this.ownerId = ownerId;
    this.senderId = senderId;
    this.type = type;
    this.sourceId = sourceId;
  }
  
  @Override
  public String getId() {
    return id;
  }
  @Override
  public void setId(String id) {
    this.id = id;
  }
  
  public Long getOwnerId() {
    return ownerId;
  }
  
  public Long getSenderId() {
    return senderId;
  }

  public Type getType() {
    return type;
  }
  
  public Long getSourceId() {
    return sourceId;
  }
  
  public static enum Type {
    FORWARDED, COMMENTED, MENTIONED_TWEET, MENTIONED_COMMENT
  }
}
