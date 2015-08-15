package sage.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class TopicReply {
  private Long id;
  private String content;
  private TopicPost topic;
  private User author;
  private Date time;

  TopicReply() {}

  public TopicReply(TopicPost topic, User author, Date time, String content) {
    this.topic = topic;
    this.author = author;
    this.time = time;
    this.content = content;
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

  @ManyToOne(optional = false)
  public TopicPost getTopic() {
    return topic;
  }
  public void setTopic(TopicPost topic) {
    this.topic = topic;
  }

  @ManyToOne(optional = false)
  public User getAuthor() {
    return author;
  }
  public void setAuthor(User author) {
    this.author = author;
  }

  public Date getTime() {
    return time;
  }
  public void setTime(Date time) {
    this.time = time;
  }
}
