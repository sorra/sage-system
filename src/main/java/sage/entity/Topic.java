package sage.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;

@Entity
public class Topic {
  private Long id;
  private String title;
  private String content;
  private User author;
  private Date createdTime;
  private Date modifiedTime;
  private Collection<Reply> replies = new ArrayList<>();

  @Id @GeneratedValue
  public Long getId() {
    return id;
  }
  void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }

  @Column(columnDefinition = "TEXT") @Lob
  public String getContent() {
    return content;
  }
  public void setContent(String content) {
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

  public Date getModifiedTime() {
    return modifiedTime;
  }
  public void setModifiedTime(Date modifiedTime) {
    this.modifiedTime = modifiedTime;
  }

  @OneToMany(mappedBy = "topic")
  public Collection<Reply> getReplies() {
    return replies;
  }
  void setReplies(Collection<Reply> replies) {
    this.replies = replies;
  }
}
