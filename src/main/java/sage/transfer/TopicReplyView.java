package sage.transfer;

import java.util.Date;

import sage.entity.TopicReply;

public class TopicReplyView {
  public Long id;
  public String content;
  public Long topicPostId;
  public UserLabel author;
  public Date time;
  /** Nullable */
  public UserLabel toUser;
  /** Nullable */
  public Long toReplyId;

  TopicReplyView() {}

  public TopicReplyView(TopicReply reply, UserLabel toUserLabel) {
    id = reply.getId();
    content = reply.getContent();
    topicPostId = reply.getTopicPost().getId();
    author = new UserLabel(reply.getAuthor());
    time = reply.getWhenCreated();
    toUser = toUserLabel;
    toReplyId = reply.getToReplyId();
  }
}
