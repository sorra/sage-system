package sage.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public abstract class Colls {
  public static <T, R> List<R> map(Collection<T> coll, Function<T, R> transformer) {
    List<R> result = new ArrayList<>(coll.size());
    for (T t : coll) {
      result.add(transformer.apply(t));
    }
    return result;
  }
}
