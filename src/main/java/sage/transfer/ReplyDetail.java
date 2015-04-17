package sage.transfer;

import java.util.Date;

import sage.entity.Reply;

public class ReplyDetail {
  public long id;
  public String content;
  public UserLabel author;
  public Date createdTime;
  public long topicId;

  ReplyDetail() {}

  public ReplyDetail(Reply reply) {
    id = reply.getId();
    content = reply.getContent();
    author = new UserLabel(reply.getAuthor());
    createdTime = reply.getCreatedTime();
    if (reply.getTopic() != null) {
      topicId = reply.getTopic().getId();
    }
  }
}
