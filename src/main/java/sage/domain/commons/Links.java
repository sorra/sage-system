package sage.domain.commons;

import java.util.OptionalInt;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

import sage.util.SemiTemplate;
import sage.util.SemiTemplate.Section;
import sage.util.Strings;

public class Links {
  public static String linksToHtml(String content) {
    return SemiTemplate.transform(content, (str, range) -> {
      int idxLinkStart = least(prefix -> str.indexOf(prefix, range.begin), "http://", "https://");
      if (idxLinkStart < 0) {
        return null;
      }
      int idxLinkEnd = least(endChar -> str.indexOf(endChar, range.begin), ' ', '，', '。', '；');
      if (idxLinkEnd < 0) {
        idxLinkEnd = str.length();
      } else {
        idxLinkEnd++;
      }
      return Section.f(str.substring(idxLinkStart, idxLinkEnd), idxLinkStart, idxLinkEnd);
    }, section -> String.format("<a href=\"%s\" title=\"%s\">%s</a>",
        section.data, section.data, Strings.omit(section.data, 50)));
  }

  @SafeVarargs
  private static <T> int least(ToIntFunction<T> func, T... candidates) {
    OptionalInt min = Stream.of(candidates).mapToInt(func).filter(i -> i >= 0).min();
    return min.orElse(-1);
  }
}
