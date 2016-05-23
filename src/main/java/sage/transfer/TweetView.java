package sage.transfer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import sage.domain.commons.IdCommons;
import sage.entity.Tag;
import sage.entity.Tweet;

public class TweetView implements Item {
  private final String type = "TweetView";

  private Long id;
  private Long authorId;
  private String authorName;
  private String avatar;
  private String content;
  private Date time;
  private TweetView origin = null;
  private MidForwards midForwards;
  private List<TagLabel> tags = new ArrayList<>();
  
  private int forwardCount;
  private int commentCount;
  
  private Long fromTag = null;
  private Long fromGroup = null;

  TweetView() {}
  
  public TweetView(Tweet tweet, Tweet origin, int forwardCount, int commentCount) {
    id = tweet.getId();
    authorId = tweet.getAuthor().getId();
    authorName = tweet.getAuthor().getName();
    avatar = tweet.getAuthor().getAvatar();
    content = convertImgLinks(tweet.getContent());
    time = tweet.getWhenCreated();
    if (origin != null) {
      this.origin = new TweetView(origin, null, 0, 0);
    }
    midForwards = tweet.midForwards();
    for (Tag tag : tweet.getTags()) {
      tags.add(new TagLabel(tag));
    }
    this.forwardCount = forwardCount;
    this.commentCount = commentCount;
  }
  
  public TweetView beFromTag(Long tagId) {
    fromTag = tagId;
    return this;
  }
  
  public TweetView beFromGroup(Long groupId) {
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
  public TweetView getOrigin() {
    return origin;
  }

  public MidForwards getMidForwards() {
    return midForwards;
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
    
    TweetView other = (TweetView) obj;
    return IdCommons.equal(getId(), other.getId());
  }

  @Override
  public String toString() {
    return authorName + ": " + content + tags;
  }
  
  private String convertImgLinks(String text) {
    int idxFirst = text.indexOf("img://");
    if (idxFirst < 0) return text;
    String body = text.substring(0, idxFirst);
    String[] links = text.substring(idxFirst).split(" ");
    for (int i = 0; i < links.length; i++) {
      String neo = "<img class=\"view-img\" src=\"" + links[i].replace("img://", "") + "\"/>";
      links[i] = neo;
    }
    return body + String.join(" ", links);
  }
}
