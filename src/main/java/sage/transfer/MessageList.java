package sage.transfer;

import java.util.List;

import sage.entity.Message;

public class MessageList {
  public List<Message> msgs;
  public UserLabel self;
  public UserLabel withUser;

  MessageList() {}

  public MessageList(List<Message> msgs, UserLabel self, UserLabel withUser) {
    this.msgs = msgs;
    this.self = self;
    this.withUser = withUser;
  }
}
