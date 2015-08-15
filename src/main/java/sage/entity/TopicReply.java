package sage.entity;

import java.util.Date;
import javax.persistence.*;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
public class TopicReply {
  private Long id;
  private String content;
  private TopicPost topicPost;
  private User author;
  private Date time;

  TopicReply() {}

  public TopicReply(TopicPost topicPost, User author, Date time, String content) {
    this.topicPost = topicPost;
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

  @ManyToOne
  @NotFound(action = NotFoundAction.IGNORE)
  @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  public TopicPost getTopicPost() {
    return topicPost;
  }
  public void setTopicPost(TopicPost topicPost) {
    this.topicPost = topicPost;
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
