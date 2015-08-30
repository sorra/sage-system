package sage.enums;

import org.junit.Assert;
import org.junit.Test;
import sage.entity.Notif.Type;

import static org.junit.Assert.assertArrayEquals;
import static sage.entity.Notif.Type.*;

public class NotifTypeTest {
  @Test
  public void type() {
    assertArrayEquals(new Type[]{
        FOLLOWED, FORWARDED, COMMENTED, REPLIED, MENTIONED_TWEET, MENTIONED_COMMENT,
        MENTIONED_TOPIC_POST, MENTIONED_TOPIC_REPLY, REPIED_IN_TOPIC}, values());
  }
}
