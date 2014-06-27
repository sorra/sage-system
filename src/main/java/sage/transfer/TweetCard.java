package sage.transfer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sage.domain.IdCommons;
import sage.entity.Tag;
import sage.entity.Tweet;

public class TweetCard implements Item {
  private final String type = "TweetCard";

  private Long id;
  private Long authorId;
  private String authorName;
  private String avatar;
  private String content;
  private Date time;
  private TweetCard origin = null;
  private String preforw = null;
  private List<TagLabel> tags = new ArrayList<>();
  
  private long forwardCount;
  private long commentCount;
  
  private Long fromTag = null;
  private Long fromGroup = null;

  TweetCard() {}
  
  public TweetCard(Tweet tweet, long forwardCount, long commentCount) {
    id = tweet.getId();
    authorId = tweet.getAuthor().getId();
    authorName = tweet.getAuthor().getName();
    avatar = tweet.getAuthor().getAvatar();
    content = tweet.getContent();
    time = tweet.getTime();
    if (tweet.getOrigin() != null) {
      origin = new TweetCard(tweet.getOrigin(), 0, 0);
    }
    preforw = tweet.getPreforw();
    for (Tag tag : tweet.getTags()) {
      tags.add(new TagLabel(tag));
    }
    this.forwardCount = forwardCount;
    this.commentCount = commentCount;
  }
  
  public TweetCard beFromTag(Long tagId) {
    fromTag = tagId;
    return this;
  }
  
  public TweetCard beFromGroup(Long groupId) {
    fromGroup = groupId;
    return this;
  }

  /**
   * used by CombineGroup
   */
  public void clearOrigin() {
    origin = null;
  }

  public Long getId() {
    return id;
  }

  public Long getAuthorId() {
    return authorId;
  }

  public String getAuthorName() {
    return authorName;
  }

  public String getAvatar() {
    return avatar;
  }

  public String getContent() {
    return content;
  }

  public Date getTime() {
    return time;
  }

  @Override
  public TweetCard getOrigin() {
    return origin;
  }

  public String getPreforw() {
    return preforw;
  }

  @Override
  public List<TagLabel> getTags() {
    return tags;
  }

  public long getForwardCount() {
    return forwardCount;
  }

  public long getCommentCount() {
    return commentCount;
  }
  
  public Long getFromTag() {
    return fromTag;
  }
  
  public Long getFromGroup() {
    return fromGroup;
  }

  @Override
  public String getType() {
    return type;
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
    
    TweetCard other = (TweetCard) obj;
    return IdCommons.equal(getId(), other.getId());
  }

  @Override
  public String toString() {
    return authorName + ": " + content + tags;
  }
  
  
}
