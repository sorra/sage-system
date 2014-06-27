package sage.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import sage.domain.IdCommons;

@Entity(name = "Blog")
public class Blog {
  private Long id;
  private String title;
  private String content;
  private User author;
  private Date time;
  private Set<Tag> tags = new HashSet<>();

  public Blog() {
  }

  public Blog(String title, String content, User author, Date time, Set<Tag> tags) {
    this.title = title;
    this.content = content;
    this.author = author;
    this.time = time;
    this.tags.addAll(tags);
  }

  @Id
  @GeneratedValue
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Column(columnDefinition = "TEXT")
  @Lob
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

  public void setAuthor(User author) {
    this.author = author;
  }

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
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
