package sage.transfer;

import sage.entity.TopicPost;

public class TopicPreview {
  public long id;
  public long groupId;
  public BlogPreview blog;
  public int replyCount;

  TopicPreview() {}

  public TopicPreview(TopicPost topic, int replyCount) {
    id = topic.getId();
    groupId = topic.getGroup().getId();
    blog = new BlogPreview(topic.getBlog());
    this.replyCount = replyCount;
  }
}
