package sage.transfer;

import java.util.Date;

import sage.entity.Topic;
import sage.util.Strings;

public class TopicPreview {
  public long id;
  public String title;
  public UserLabel author;
  public String preview;
  public Date createdTime;

  TopicPreview() {}

  public TopicPreview(Topic topic) {
    id = topic.getId();
    title = topic.getTitle();
    author = new UserLabel(topic.getAuthor());
    preview = Strings.cut(topic.getContent(), 0, 100);
    createdTime = topic.getCreatedTime();
  }
}
