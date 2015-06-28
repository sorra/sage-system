package sage.enums;

import org.junit.Test;
import sage.entity.TagChangeRequest.Status;
import sage.entity.TagChangeRequest.Type;

import static org.junit.Assert.assertArrayEquals;

public class TagChangeRequestEnumTest {
  @Test
  public void status() {
    assertArrayEquals(new Status[]{Status.PENDING, Status.CANCELED, Status.ACCEPTED, Status.REJECTED},
        Status.values());
  }

  @Test
  public void type() {
    assertArrayEquals(new Type[]{Type.MOVE, Type.RENAME, Type.SET_INTRO},
        Type.values());
  }
}
