package sage.transfer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import sage.entity.Follow;
import sage.entity.User;

public class UserCard {
  private long id;
  private String name;
  private String avatar;
  private String intro;
  private Date whenCreated;

  private int followerCount;
  private int blogCount;
  private int tweetCount;

  private boolean isFollowing;
  private boolean isFollower;

  private List<TagLabel> tags = new ArrayList<>();
  private UserCardFollow follow;

  UserCard() {}
  
  public UserCard(User user, int _followerCount, int _blogCount, int _tweetCount,
      Follow followFromCurrentUser, Follow followToCurrentUser, Collection<TagLabel> _tags) {
    id = user.getId();
    name = user.getName();
    avatar = user.getAvatar();
    intro = user.getIntro();
    whenCreated = user.getWhenCreated();

    followerCount = _followerCount;
    blogCount = _blogCount;
    tweetCount = _tweetCount;

    isFollowing = followFromCurrentUser != null;
    isFollower = followToCurrentUser != null;

    tags.addAll(_tags);
    if (followFromCurrentUser != null) {
      follow = new UserCardFollow(followFromCurrentUser);
    }
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getAvatar() {
    return avatar;
  }

  public String getIntro() {
    return intro;
  }

  public Date getWhenCreated() {
    return whenCreated;
  }

  public int getFollowerCount() {
    return followerCount;
  }

  public int getBlogCount() {
    return blogCount;
  }

  public int getTweetCount() {
    return tweetCount;
  }

  public boolean getIsFollowing() {
    return isFollowing;
  }

  public boolean getIsFollower() {
    return isFollower;
  }

  public List<TagLabel> getTags() {
    return tags;
  }

  public UserCardFollow getFollow() {
    return follow;
  }
}
