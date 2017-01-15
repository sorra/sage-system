package sage.enums;

import org.junit.Test;
import sage.entity.Notification.Type;

import static org.junit.Assert.assertArrayEquals;
import static sage.entity.Notification.Type.*;

public class NotificationTypeTest {
  @SuppressWarnings("deprecation")
  @Test
  public void type() {
    assertArrayEquals(new Type[]{
        FOLLOWED, FORWARDED, COMMENTED, REPLIED_IN_COMMENT, MENTIONED_TWEET, MENTIONED_COMMENT,
        MENTIONED_TOPIC_POST, MENTIONED_TOPIC_REPLY, REPIED_IN_TOPIC, MENTIONED_BLOG}, values());
  }
}
