package sage.entity;

import java.util.ArrayList;
import java.util.Collection;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import sage.domain.IdCommons;

@Entity(name = "Tweet")
public class Tweet {
  private Long id;
  private String content;
  private User author;
  private Date time;
  private Tweet origin = null;
  /**
   * Flattened prefix for nested forwards
   * @see TweetPostService#enPreforw()
   */
  private String preforw = null;
  private Long blogId = null;
  private Set<Tag> tags = new HashSet<>();
  private Collection<Comment> comments = new ArrayList<>();

  public Tweet() {
  }

  public Tweet(String content, User author, Date time, Set<Tag> tags) {
    this.content = content;
    this.author = author;
    this.time = time;
    this.tags.addAll(tags);
  }

  public Tweet(String content, User author, Date time, Tweet initialOrigin) {
    this(content, author, time, initialOrigin.getTags());
    setOrigin(initialOrigin);
    if (initialOrigin.getOrigin() != null) {
      throw new IllegalArgumentException("tweet's origin should not be nested!");
    }
  }

  public Tweet(String content, User author, Date time, Tweet origin, String preforw) {
    this(content, author, time, origin);
    setPreforw(preforw);
  }

  public Tweet(String content, User author, Date time, Blog sourceBlog) {
    this(content, author, time, sourceBlog.getTags());
    setBlogId(sourceBlog.getId());
  }

  @Id
  @GeneratedValue
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  @OneToOne
  public Tweet getOrigin() {
    return origin;
  }

  public void setOrigin(Tweet origin) {
    this.origin = origin;
  }

  public String getPreforw() {
    return preforw;
  }

  public void setPreforw(String preforw) {
    this.preforw = preforw;
  }

  public Long getBlogId() {
    return blogId;
  }

  public void setBlogId(Long blogId) {
    this.blogId = blogId;
  }

  @ManyToMany(fetch = FetchType.EAGER)
  public Set<Tag> getTags() {
    return tags;
  }

  public void setTags(Set<Tag> tags) {
    this.tags = tags;
  }

  @OneToMany(mappedBy = "source")
  public Collection<Comment> getComments() {
    return comments;
  }

  public void setComments(Collection<Comment> comments) {
    this.comments = comments;
  }

  @Override
  public String toString() {
    return author + ": " + content + tags;
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
    
    Tweet other = (Tweet) obj;
    return IdCommons.equal(getId(), other.getId());
  }
}
