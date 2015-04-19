package sage.entity;

import java.util.*;
import javax.persistence.*;

@Entity
public class Group {
  private Long id;
  private String name;
  private String introduction;
  private Set<Tag> tags;
  private User creator;
  private Date createdTime;
  private Set<User> members = new HashSet<>();

  @Id @GeneratedValue
  public Long getId() {
    return id;
  }
  void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  public String getIntroduction() {
    return introduction;
  }
  public void setIntroduction(String introduction) {
    this.introduction = introduction;
  }

  @ManyToMany(fetch = FetchType.EAGER)
  public Set<Tag> getTags() {
    return tags;
  }
  public void setTags(Set<Tag> tags) {
    this.tags = tags;
  }

  @ManyToOne
  public User getCreator() {
    return creator;
  }
  public void setCreator(User creator) {
    this.creator = creator;
  }

  public Date getCreatedTime() {
    return createdTime;
  }
  public void setCreatedTime(Date createdTime) {
    this.createdTime = createdTime;
  }

  @ManyToMany
  public Set<User> getMembers() {
    return members;
  }
  public void setMembers(Set<User> members) {
    this.members = members;
  }
}
