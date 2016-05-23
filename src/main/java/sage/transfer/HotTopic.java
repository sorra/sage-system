package sage.transfer;

import java.util.Date;

import sage.entity.TopicPost;

public class HotTopic implements Comparable<HotTopic> {
  public long id;
  public TopicPreview topic;
  public int replyCount;
  public Date lastActiveTime;
  public double rank;

  HotTopic() {}

  public HotTopic(TopicPreview topic, int replyCount, Date lastReplyTime) {
    id = topic.getId();
    this.topic = topic;
    this.replyCount = replyCount;
    lastActiveTime = lastReplyTime!=null ? lastReplyTime : topic.getWhenCreated();
  }

  @Override
  public int compareTo(HotTopic o) {
    return -Double.compare(rank, o.rank);
  }
}
