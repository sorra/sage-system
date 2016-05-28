package sage.domain.commons;

import sage.util.SemiTemplate;
import sage.util.SemiTemplate.Section;

public class Markdown {
  // "  \n" is Markdown paragraph mark. We convert every "\n" to paragraph mark
  public static String addBlankRow(String text) {
    return SemiTemplate.transform(text, (str, range) -> {
      int idxLF = str.indexOf('\n', range.begin);
      if (idxLF >= 2
          && str.charAt(idxLF-1) == ' ' && str.charAt(idxLF-2) == ' ') {
        return null; // Already has '  \n', skip
      }
      return Section.substr(str, idxLF, idxLF+1);
    }, section -> "  " + section.data);
  }
}
