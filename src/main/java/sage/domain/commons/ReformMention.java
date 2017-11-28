package sage.domain.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReformMention {
  private static final Logger log = LoggerFactory.getLogger(ReformMention.class);
  public static String apply(String content) {
    if (content == null || content.isEmpty()) return content;
    try {
      return recur(content);
    } catch (Exception e) {
      log.error("Escape from exception.", e);
      return content;
    }
  }

  private static String recur(String content) {
    int indexOfAt = content.indexOf('@');
    int indexOfSpace = content.indexOf(' ', indexOfAt);
    if (indexOfSpace < 0) {
      indexOfSpace = content.length();
    }
    indexOfAt = content.lastIndexOf('@', indexOfSpace - 1);
    int indexOfSharp = content.indexOf('#', indexOfAt);

    if (indexOfAt >= 0 && indexOfSpace > 0 && indexOfSharp > 0 && indexOfSharp < indexOfSpace) {
      String name = content.substring(indexOfAt + 1, indexOfSharp);
      Long id = safeToLong(content.substring(indexOfSharp + 1, indexOfSpace));
      String done;
      if (name.length() > 0 && id != null) {
        done = content.substring(0, indexOfAt) + String.format("<a uid=\"%s\" href=\"/user/%s\">@%s</a>", id, id, name);
      } else {
        done = content.substring(0, indexOfSpace);
      }
      String rest = indexOfSpace < content.length() ? recur(content.substring(indexOfSpace, content.length())) : "";
      return done + rest;
    }
    return content;
  }

  private static Long safeToLong(String s) {
    try {
      return Long.valueOf(s);
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
