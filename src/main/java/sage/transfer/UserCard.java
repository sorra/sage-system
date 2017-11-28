package sage.transfer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

  public Date getWhenCreated() {
    return whenCreated;
  }

  public void setWhenCreated(Date whenCreated) {
    this.whenCreated = whenCreated;
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

  public boolean isFollowing() {
    return isFollowing;
  }

  public void setFollowing(boolean following) {
    isFollowing = following;
  }

  public boolean isFollower() {
    return isFollower;
  }

  public void setFollower(boolean follower) {
    isFollower = follower;
  }

  public List<TagLabel> getTags() {
    return tags;
  }

  public void setTags(List<TagLabel> tags) {
    this.tags = tags;
  }

  public UserCardFollow getFollow() {
    return follow;
  }

  public void setFollow(UserCardFollow follow) {
    this.follow = follow;
  }
}
