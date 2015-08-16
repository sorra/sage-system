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
  private Long toUserId;
  private Long toReplyId;

  TopicReply() {}

  public TopicReply(TopicPost topicPost, User author, Date time, String content) {
    this.topicPost = topicPost;
    this.author = author;
    this.time = time;
    this.content = content;
    this.toReplyId = toReplyId;
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

  public Long getToUserId() {
    return toUserId;
  }
  public void setToUserId(Long toUserId) {
    this.toUserId = toUserId;
  }

  public Long getToReplyId() {
    return toReplyId;
  }
  public void setToReplyId(Long toReplyId) {
    this.toReplyId = toReplyId;
  }

  public TopicReply setToInfo(Long toUserId, Long toReplyId) {
    this.toUserId = toUserId;
    this.toReplyId = toReplyId;
    return this;
  }
}
