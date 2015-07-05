package sage.entity;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "tagId"}),
    indexes = {@Index(columnList = "userId"), @Index(columnList = "tagId")})
public class UserTag {
  private Long id;
  private long userId;
  private long tagId;

  UserTag() {}

  public UserTag(long userId, long tagId) {
    this.userId = userId;
    this.tagId = tagId;
  }

  @Id @GeneratedValue
  public Long getId() {
    return id;
  }
  void setId(Long id) {
    this.id = id;
  }

  public long getUserId() {
    return userId;
  }
  void setUserId(long userId) {
    this.userId = userId;
  }

  public long getTagId() {
    return tagId;
  }
  void setTagId(long tagId) {
    this.tagId = tagId;
  }
}
