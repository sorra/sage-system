package sage.transfer;

import java.util.Date;
import java.util.function.Function;

import sage.entity.Topic;
import sage.entity.User;

public class TopicDetail {
  public long id;
  public String title;
  public UserCard author;
  public String content;
  public Date createdTime;
  public Date modifiedTime;

  TopicDetail() {}

  public TopicDetail(Topic topic, Function<User, UserCard> userToCard) {
    id = topic.getId();
    title = topic.getTitle();
    author = userToCard.apply(topic.getAuthor());
    content = topic.getContent();
    createdTime = topic.getCreatedTime();
    modifiedTime = topic.getModifiedTime();
  }
}
