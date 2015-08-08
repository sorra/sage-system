package sage.util;

import java.util.*;
import java.util.function.Function;

public abstract class Colls {
  public static <T, R> List<R> map(Collection<T> coll, Function<T, R> transformer) {
    List<R> result = new ArrayList<>(coll.size());
    for (T t : coll) {
      result.add(transformer.apply(t));
    }
    return result;
  }

  public static <T, R> List<R> flatMap(Collection<T> coll, Function<T, Collection<R>> transformer) {
    List<R> result = new ArrayList<>();
    for (T t: coll) {
      result.addAll(transformer.apply(t));
    }
    return result;
  }

  @SafeVarargs
  public static <T> List<T> copy(Collection<T>... colls) {
    List<T> list = new ArrayList<>();
    for (Collection<T> coll : colls) {
      list.addAll(coll);
    }
    return list;
  }

  @SafeVarargs
  public static <T> List<T> copySort(Comparator<T> comparator, Collection<T>... colls) {
    List<T> list = copy(colls);
    Collections.sort(list, comparator);
    return list;
  }

  public static <K, V> Map<K, V> mapOfValues(Collection<V> values, Function<V, K> keyExtractor) {
    Map<K, V> map = new HashMap<>();
    values.forEach(v -> map.put(keyExtractor.apply(v), v));
    return map;
  }
}
