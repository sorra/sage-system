package sage.entity;

import java.util.Date;
import javax.persistence.*;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
public class Reply {
  private Long id;
  private String content;
  private User author;
  private Date createdTime;
  private Topic topic;

  @Id @GeneratedValue
  public Long getId() {
    return id;
  }
  void setId(Long id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }
  void setContent(String content) {
    this.content = content;
  }

  @OneToOne(optional = false)
  public User getAuthor() {
    return author;
  }
  void setAuthor(User author) {
    this.author = author;
  }

  public Date getCreatedTime() {
    return createdTime;
  }
  void setCreatedTime(Date createdTime) {
    this.createdTime = createdTime;
  }

  @ManyToOne
  @NotFound(action = NotFoundAction.IGNORE)
  @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  public Topic getTopic() {
    return topic;
  }
  void setTopic(Topic topic) {
    this.topic = topic;
  }
}
