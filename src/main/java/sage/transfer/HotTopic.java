package sage.transfer;

import java.util.Date;

import sage.entity.TopicPost;

public class HotTopic implements Comparable<HotTopic> {
  public long id;
  public long groupId;
  public String groupName;
  public BlogPreview blog;
  public int replyCount;
  public Date lastActiveTime;
  public double rank;

  HotTopic() {}

  public HotTopic(TopicPost topic, int replyCount, Date lastReplyTime) {
    id = topic.getId();
    groupId = topic.getGroup().getId();
    groupName = topic.getGroup().getName();
    blog = new BlogPreview(topic.getBlog());
    this.replyCount = replyCount;
    lastActiveTime = lastReplyTime!=null ? lastReplyTime : topic.getWhenCreated();
  }

  @Override
  public int compareTo(HotTopic o) {
    return -Double.compare(rank, o.rank);
  }
}
