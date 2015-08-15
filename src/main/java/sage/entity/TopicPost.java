package sage.entity;

import java.util.Date;
import javax.persistence.*;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
public class TopicPost {
  private Long id;
  private Blog blog;
  private User author;
  private Date time;
  private Group group;
  private boolean hidden = false;

  TopicPost() {}

  public TopicPost(Blog blog, Group group) {
    this.blog = blog;
    this.group = group;
  }

  @Id @GeneratedValue
  public Long getId() {
    return id;
  }
  void setId(Long id) {
    this.id = id;
  }

  @ManyToOne(optional = false)
  public Blog getBlog() {
    return blog;
  }
  void setBlog(Blog blog) {
    this.blog = blog;
  }

  @ManyToOne
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

  @ManyToOne
  @NotFound(action = NotFoundAction.IGNORE)
  @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  public Group getGroup() {
    return group;
  }
  public void setGroup(Group group) {
    this.group = group;
  }

  public boolean isHidden() {
    return hidden;
  }
  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }
}
