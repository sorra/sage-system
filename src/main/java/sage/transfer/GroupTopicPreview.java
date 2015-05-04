package sage.transfer;

import sage.entity.GroupTopic;

public class GroupTopicPreview {
  public long id;
  public long groupId;
  public BlogPreview blog;

  GroupTopicPreview() {}

  public GroupTopicPreview(GroupTopic topic) {
    id = topic.getId();
    groupId = topic.getGroup().getId();
    blog = new BlogPreview(topic.getBlog());
  }
}
