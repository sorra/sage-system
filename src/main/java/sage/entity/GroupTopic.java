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
  private boolean hidden = false;

  GroupTopic() {}

  public GroupTopic(Blog blog, Group group) {
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

  @ManyToOne(optional = false)
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
