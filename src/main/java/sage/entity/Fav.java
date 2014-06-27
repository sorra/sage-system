package sage.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import sage.domain.IdCommons;

@Entity(name = "Fav")
public class Fav {
  private Long id;
  private String link;
  private User owner;
  private Date time;
  
  public Fav() {}
  
  public Fav(String link, User owner, Date time) {
    this.link = link;
    this.owner = owner;
    this.time = time;
  }
  
  @Id @GeneratedValue
  public Long getId() {return id;}
  public void setId(Long id) {this.id = id;}
  
  public String getLink() {return link;}
  public void setLink(String link) {this.link = link;}
  
  @OneToOne
  public User getOwner() {return owner;}
  public void setOwner(User owner) {this.owner = owner;}
  
  public Date getTime() {return time;}
  public void setTime(Date time) {this.time = time;}
  
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
    
    Fav other = (Fav) obj;
    return IdCommons.equal(getId(), other.getId());
  }
}
