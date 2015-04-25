package sage.entity;

import java.util.*;
import javax.persistence.*;

@Entity
@Table(name = "groupp")
public class Group {
  private Long id;
  private String name;
  private String introduction;
  private Set<Tag> tags;
  private User creator;
  private Date createdTime;
  private Set<User> members = new HashSet<>();

  Group() {}

  public Group(String name, String introduction, Set<Tag> tags, User creator, Date createdTime) {
    this.name = name;
    this.introduction = introduction;
    this.tags = tags;
    this.creator = creator;
    this.createdTime = createdTime;
    this.members.add(creator);
  }

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

  @ManyToOne(optional = false)
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

  @ManyToMany @JoinTable(name = "groupp_member")
  public Set<User> getMembers() {
    return members;
  }
  public void setMembers(Set<User> members) {
    this.members = members;
  }
}
