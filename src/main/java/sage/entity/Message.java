package sage.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Message {
  private Long id;
  private String content;
  private Long fromUser;
  private Long toUser;

  Message() {}

  public Message(String content, Long fromUser, Long toUser) {
    this.content = content;
    this.fromUser = fromUser;
    this.toUser = toUser;
  }

  @Id @GeneratedValue
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }
  public void setContent(String content) {
    this.content = content;
  }

  public Long getFromUser() {
    return fromUser;
  }
  public void setFromUser(Long fromUser) {
    this.fromUser = fromUser;
  }

  public Long getToUser() {
    return toUser;
  }
  public void setToUser(Long toUser) {
    this.toUser = toUser;
  }
}
