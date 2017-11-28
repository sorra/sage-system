package sage.transfer;

import java.util.ArrayList;
import java.util.Collection;

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

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public String getIntro() {
    return intro;
  }

  public void setIntro(String intro) {
    this.intro = intro;
  }

  public int getFollowingCount() {
    return followingCount;
  }

  public void setFollowingCount(int followingCount) {
    this.followingCount = followingCount;
  }

  public int getFollowerCount() {
    return followerCount;
  }

  public void setFollowerCount(int followerCount) {
    this.followerCount = followerCount;
  }

  public int getBlogCount() {
    return blogCount;
  }

  public void setBlogCount(int blogCount) {
    this.blogCount = blogCount;
  }

  public int getTweetCount() {
    return tweetCount;
  }

  public void setTweetCount(int tweetCount) {
    this.tweetCount = tweetCount;
  }

  public Collection<TagLabel> getTopTags() {
    return topTags;
  }

  public void setTopTags(Collection<TagLabel> topTags) {
    this.topTags = topTags;
  }
}
