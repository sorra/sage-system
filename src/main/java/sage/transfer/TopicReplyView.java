package sage.transfer;

import java.sql.Timestamp;

import sage.entity.TopicReply;

public class TopicReplyView {
  public Long id;
  public String content;
  public Long topicPostId;
  public UserLabel author;
  public Timestamp whenCreated;
  /** Nullable */
  public UserLabel toUser;
  /** Nullable */
  public Long toReplyId;

  TopicReplyView() {}

  public TopicReplyView(TopicReply reply, UserLabel toUserLabel) {
    id = reply.getId();
    content = reply.getContent();
    topicPostId = reply.getTopicPostId();
    author = new UserLabel(reply.getAuthor());
    whenCreated = reply.getWhenCreated();
    toUser = toUserLabel;
    toReplyId = reply.getToReplyId();
  }
}
