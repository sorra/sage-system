package sage.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class SemiTemplate {

  //Main entrance
  public static <T> String transform(String str,
      BiFunction<String, Range, Section<T>> matcher, Function<Section<T>, String> producer) {
    return produce(match(str, matcher), producer);
  }
  
  public static <T> List<Object> match(String str, BiFunction<String, Range, Section<T>> matcher) {
    final int len = str.length();
    final List<Object> pieces = new ArrayList<>();
    Range current = new Range(0, len);
    while (true) {
      Section<T> match = matcher.apply(str, current);
      if (match == null) {
        // null means no more match
        pieces.add(str.substring(current.begin, current.end));
        return pieces;
      }
      
      if (match.begin > current.begin) {
        pieces.add(str.substring(current.begin, match.begin));
      } else if (match.begin < current.begin) {
        throw new IllegalArgumentException("Wrong matcher implementation!");
      }
      pieces.add(match);
      
      if (match.end == current.end) {
        return pieces;
      } else if (match.end > current.end) {
        throw new IllegalArgumentException("Wrong matcher implementation!");
      }
      // Continue from the rest
      current = new Range(match.end, len);
    }
  }
  
  @SuppressWarnings("unchecked")
  public static <T> String produce(List<Object> pieces, Function<T, String> producer) {
    StringBuilder sb = new StringBuilder();
    for (Object piece : pieces) {
      if (piece instanceof String) {
        sb.append(piece);
      } else {
        sb.append(producer.apply((T) piece));
      }
    }
    return sb.toString();
  }

  //Test
  public static void main(String[] args) {
    String input = "@1 @11 @111";

    String result = transform(input,
        (str, range) -> {
          int idxOfAt = str.indexOf('@', range.begin);
          if (idxOfAt < 0) {
            return null;
          }

          int idxOfEnd = range.end;
          for (int i = idxOfAt+1; i < range.end; i++) {
            if (!Character.isDigit(str.charAt(i))) {
              idxOfEnd = i;
              break;
            }
          }

          if (idxOfAt+1 == idxOfEnd) {
            return null;
          }
          int num = Integer.parseInt(str.substring(idxOfAt+1, idxOfEnd));
          return Section.f(num, idxOfAt, idxOfEnd);
        },

        section -> "#" + section.data * 2);

    System.out.println(result);
  }
  
  public static class Range {
    /** Inclusive */
    public final int begin;
    /** Exclusive */
    public final int end;
    
    public Range(int begin, int end) {
      this.begin = begin;
      this.end = end;
    }
    @Override
    public String toString() {
      return "range("+begin+", "+end+")";
    }
  }
  
  public static class Section<T> {
    public final T data;
    public final int begin;
    public final int end;
    
    public Section(T data, int begin, int end) {
      this.data = data;
      this.begin = begin;
      this.end = end;
    }
    
    public static <T> Section<T> f(T data, int begin, int end) {
      return new Section<T>(data, begin, end);
    }
    
    public static Section<String> substr(String str, int begin, int end) {
      return new Section<String>(str.substring(begin, end), begin, end);
    }
    
    public String toString() {
      return String.format("section(data=%s, begin=%d, end=%d)", data, begin, end);
    }
  }
}
