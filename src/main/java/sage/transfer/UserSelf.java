package sage.transfer;

import java.util.ArrayList;
import java.util.Collection;

import sage.entity.User;

public class UserSelf {
  private long id;
  private String name;
  private String avatar;
  private String intro;

  private int followingCount;
  private int followerCount;
  private int blogCount;
  private int tweetCount;

  private Collection<TagLabel> topTags = new ArrayList<>();

  UserSelf() {}
  
  public UserSelf(User user, int _followingCount, int _followerCount, int _blogCount,
      int _tweetCount,
      Collection<TagLabel> _topTags) {
    id = user.getId();
    name = user.getName();
    avatar = user.getAvatar();
    intro = user.getIntro();

    followingCount = _followingCount;
    followerCount = _followerCount;
    blogCount = _blogCount;
    tweetCount = _tweetCount;

    topTags.addAll(_topTags);
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

  public int getFollowingCount() {
    return followingCount;
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

  public Collection<TagLabel> getTopTags() {
    return topTags;
  }
}
