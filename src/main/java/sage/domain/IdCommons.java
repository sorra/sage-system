package sage.domain;

import java.util.Objects;

public abstract class IdCommons {
  public static boolean equal(Long id1, Long id2) {
    if (id1 == null || id2 == null) {
      // Here let null != null
      return false;
    }
    return id1.equals(id2);
  }
  
  public static int hashCode(Long id) {
    return Objects.hashCode(id);
  }
}
