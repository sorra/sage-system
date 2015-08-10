package sage.transfer;

import sage.entity.Message;

public class ConversationPreview {
  public UserLabel self;
  public UserLabel withUser;
  public Message lastMsg;

  ConversationPreview() {}

  public ConversationPreview(UserLabel self, UserLabel withUser, Message lastMsg) {
    this.self = self;
    this.withUser = withUser;
    this.lastMsg = lastMsg;
  }
}
