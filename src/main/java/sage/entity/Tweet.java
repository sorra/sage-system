package sage.entity;

import java.util.*;
import javax.persistence.*;

import sage.domain.IdCommons;
import sage.transfer.MidForwards;

@Entity(name = "Tweet")
public class Tweet {
  private Long id;
  private String content;
  private User author;
  private Date time;

  private Long originId = -1L;

  private String midForwardsJson = null;
  private Long blogId = -1L;
  private Set<Tag> tags = new HashSet<>();
  private Collection<Comment> comments = new ArrayList<>();

  Tweet() {}

  public Tweet(String content, User author, Date time, Set<Tag> tags) {
    this.content = content;
    this.author = author;
    this.time = time;
    this.tags.addAll(tags);
  }

  public Tweet(String content, User author, Date time, Tweet initialOrigin) {
    this(content, author, time, initialOrigin.getTags());
    setOriginId(initialOrigin.getId());
    if (initialOrigin.hasOrigin()) {
      throw new IllegalArgumentException("tweet's origin should not be nested!");
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

  @Column(nullable = false)
  public Long getOriginId() {
    return originId;
  }
  public void setOriginId(Long originId) {
    this.originId = originId;
  }
  public boolean hasOrigin() {
    return originId >= 0;
  }

  public String getMidForwardsJson() {
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
  public boolean hasBlog() {
    return blogId >= 0;
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
