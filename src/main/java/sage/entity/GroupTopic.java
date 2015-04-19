package sage.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class GroupTopic {
  private Long id;
  private Blog blog;
  private Group group;
  private boolean hidden = true;

  GroupTopic() {}

  public GroupTopic(Blog blog) {
    this.blog = blog;
  }

  @Id @GeneratedValue
  public Long getId() {
    return id;
  }
  void setId(Long id) {
    this.id = id;
  }

  @ManyToOne
  public Blog getBlog() {
    return blog;
  }
  void setBlog(Blog blog) {
    this.blog = blog;
  }

  @ManyToOne
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
