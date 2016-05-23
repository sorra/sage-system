package sage;

import org.junit.Test;
import sage.domain.commons.ReformMention;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

public class ReformMentionTest {
  @Test
  public void test() {
    String one = ReformMention.apply("@Admin#1");
    String multi = ReformMention.apply("@Admin#1 @Bethia#2 哈哈@CentKuma#3");
    String msharpInvalid = ReformMention.apply("@Admin#1#2");
    String msharpValid = ReformMention.apply("@Admin#1 #2");
    String mat = ReformMention.apply("@@Bethia#2");
    String mat2 = ReformMention.apply("@V@Bethia#2");
    String mat3 = ReformMention.apply("@V#3@CentKuma#3");

    String tmpl = "<a uid=\"%s\" href=\"/user/%s\">@%s</a>";
    String admin = format(tmpl, 1, 1, "Admin");
    String bethia = format(tmpl, 2, 2, "Bethia");
    String centkuma = format(tmpl, 3, 3, "CentKuma");

    assertEquals(admin, one);
    assertEquals(admin+" "+bethia+" 哈哈"+centkuma, multi);
    assertEquals("@Admin#1#2", msharpInvalid);
    assertEquals(admin+" #2", msharpValid);
    assertEquals("@"+bethia, mat);
    assertEquals("@V"+bethia, mat2);
    assertEquals("@V#3"+centkuma, mat3);
  }
}
