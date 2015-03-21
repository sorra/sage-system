package sage.entity;

import javax.persistence.*;

@Entity
public class FollowListHeed {
  private Long id;
  private Long userId;
  private FollowListEntity list;

  FollowListHeed() {}

  public FollowListHeed(long userId, FollowListEntity list) {
    this.userId = userId;
    this.list = list;
  }

  @Id @GeneratedValue
  public Long getId() {
    return id;
  }
  void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }
  void setUserId(Long userId) {
    this.userId = userId;
  }

  @ManyToOne
  public FollowListEntity getList() {
    return list;
  }
  void setList(FollowListEntity list) {
    this.list = list;
  }
}
