package sage.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import sage.domain.IdCommons;

@Entity(name = "HeededTag")
public class HeededTag {
  private Long id;
  private User user;
  private Tag tag;
  
  public HeededTag(User user, Tag tag) {
    this.user = user;
    this.tag = tag;
  }
  
  @Id @GeneratedValue
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  
  @OneToOne
  public User getUser() {
    return user;
  }
  public void setUser(User user) {
    this.user = user;
  }
  
  @OneToOne
  public Tag getTag() {
    return tag;
  }
  public void setTag(Tag tag) {
    this.tag = tag;
  }
  
  @Override
  public int hashCode() {
    return IdCommons.hashCode(getId());
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    
    HeededTag other = (HeededTag) obj;
    return IdCommons.equal(getId(), other.getId());
  }
}
