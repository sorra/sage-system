package sage;

import java.util.HashSet;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import sage.domain.commons.ReplaceMention;
import sage.domain.repository.UserRepository;
import sage.entity.User;

import static org.junit.Assert.assertEquals;

public class ReplaceMentionTest {
  @Test
  public void test() {
    String content = "@Admin @Bethia XXX@Admin@Bethia@CentOS社区 ";
    content = "@Admin @Admin@Admin@Admin@Admin@Admin @Admin@Admin @Admin@Admin@Admin @Admi";
    UserRepository ur = new UserRepository() {
      @Override
      public User findByName(String name) {
        if (name.equals("Admi")) return null;
        User user = new User("admin@a.a", "123");
        ReflectionTestUtils.setField(user, "id", 1000L);
        user.setName(name);
        return user;
      }
    };
    String output = ReplaceMention.with(ur).apply(content, new HashSet<>());
    assertEquals(
        "@Admin#1000 @Admin@Admin@Admin@Admin@Admin#1000 @Admin@Admin#1000 @Admin@Admin@Admin#1000 @Admi",
        output);
  }
}
