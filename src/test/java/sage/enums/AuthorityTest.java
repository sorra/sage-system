package sage.enums;

import org.junit.Assert;
import org.junit.Test;
import sage.domain.constraints.Authority;

import static sage.domain.constraints.Authority.*;

public class AuthorityTest {
  @Test
  public void authority() {
    Assert.assertArrayEquals(new Authority[]{USER, TAG_ADMIN, SITE_ADMIN}, values());
  }
}
