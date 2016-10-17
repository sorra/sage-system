package sage.domain.commons;

import java.util.OptionalInt;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

import sage.util.SemiTemplate;
import sage.util.SemiTemplate.Section;

public class Links {

  public static Section<String> match(String str, SemiTemplate.Range range) {
    int idxLinkStart = least(prefix -> str.indexOf(prefix, range.begin), "http://", "https://");
    if (idxLinkStart < 0) {
      return null;
    }
    int idxLinkEnd = least(endChar -> str.indexOf(endChar, idxLinkStart), ' ', '，', '。', '；', '、', '）', '\n', '\r', '\t');
    if (idxLinkEnd < 0) {
      idxLinkEnd = str.length();
    }
    return Section.substr(str, idxLinkStart, idxLinkEnd);
  }

  @SafeVarargs
  private static <T> int least(ToIntFunction<T> func, T... candidates) {
    OptionalInt min = Stream.of(candidates).mapToInt(func).filter(i -> i >= 0).min();
    return min.orElse(-1);
  }
}
