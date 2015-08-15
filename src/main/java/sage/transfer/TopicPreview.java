package sage.transfer;

import sage.entity.TopicPost;

public class TopicPreview {
  public long id;
  public long groupId;
  public BlogPreview blog;

  TopicPreview() {}

  public TopicPreview(TopicPost topic) {
    id = topic.getId();
    groupId = topic.getGroup().getId();
    blog = new BlogPreview(topic.getBlog());
  }
}
