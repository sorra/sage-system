package sage.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

import sage.domain.commons.IdCommons;

@Entity
public class Blog {
  private Long id;
  private String title;
  private String content;
  private User author;
  private Date createdTime;
  private Date modifiedTime;
  private Set<Tag> tags = new HashSet<>();

  Blog() {}

  public Blog(String title, String content, User author, Date createdTime, Set<Tag> tags) {
    this.title = title;
    this.content = content;
    this.author = author;
    this.createdTime = createdTime;
    this.tags.addAll(tags);
  }

  @Id
  @GeneratedValue
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

  @ManyToOne(optional = false)
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

  @ManyToMany(fetch = FetchType.EAGER)
  public Set<Tag> getTags() {
    return tags;
  }
  public void setTags(Set<Tag> tags) {
    this.tags = tags;
  }

  @Override
  public String toString() {
    return author + ": " + title + tags
        + "\n" + content;
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
    
    Blog other = (Blog) obj;
    return IdCommons.equal(getId(), other.getId());
  }
}
