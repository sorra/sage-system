package sage.entity;

import java.util.*;
import javax.persistence.*;

import sage.domain.commons.IdCommons;
import sage.transfer.MidForwards;

@Entity
public class Tweet {
  private Long id;
  private String content;
  private User author;
  private Date time;
  private Long timeMillis;

  private Long originId = -1L;

  private String midForwardsJson = null;
  private Long blogId = -1L;
  private Set<Tag> tags = new HashSet<>();
  private Collection<Comment> comments = new ArrayList<>();

  private boolean deleted;

  Tweet() {}

  public Tweet(String content, User author, Date time, Set<Tag> tags) {
    this.content = content;
    this.author = author;
    this.time = time;
    this.timeMillis = time.getTime();
    this.tags.addAll(tags);
  }

  public Tweet(String content, User author, Date time, Tweet initialOrigin) {
    this(content, author, time, initialOrigin.getTags());
    setOriginId(initialOrigin.getId());
    if (initialOrigin.hasOrigin()) {
      throw new IllegalArgumentException(String.format(
          "tweet's origin should not be nested! initialOrigin[%s] and its origin[%s]",
          initialOrigin.getId(), initialOrigin.getOriginId()));
    }
  }

  public Tweet(String content, User author, Date time, Tweet origin, MidForwards midForwards) {
    this(content, author, time, origin);
    setMidForwardsJson(midForwards.toJson());
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
    if (isDeleted()) return "";
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

  public Long getTimeMillis() {
    return timeMillis;
  }
  public void setTimeMillis(Long timeMillis) {
    this.timeMillis = timeMillis;
  }

  @Column(nullable = false)
  public Long getOriginId() {
    if (isDeleted()) return -1L;
    return originId;
  }
  public void setOriginId(Long originId) {
    this.originId = originId;
  }
  public boolean hasOrigin() {
    return originId >= 0;
  }

  public String getMidForwardsJson() {
    if (isDeleted()) return null;
    return midForwardsJson;
  }
  public void setMidForwardsJson(String midForwardsJson) {
    this.midForwardsJson = midForwardsJson;
  }

  @Column(nullable = false)
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

  public boolean isDeleted() {
    return deleted;
  }
  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public boolean hasBlog() {
    return blogId >= 0;
  }

  public MidForwards midForwards() {
    String json = getMidForwardsJson();
    return json == null ? null : MidForwards.fromJson(json);
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
