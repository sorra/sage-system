package sage.domain.commons;

import java.util.Set;
import java.util.function.Function;

import sage.entity.User;

public class ReplaceMention {
  private Function<String, User> findByName;

  public static ReplaceMention with(Function<String, User> findByName) {
    ReplaceMention instance = new ReplaceMention();
    instance.findByName = findByName;
    return instance;
  }

  public String apply(String content, Set<Long> mentionedIds) {
    if (content == null || content.isEmpty()) return content;
    return recur(content, 0, new StringBuilder(), mentionedIds);
  }

  /*
 * Replace "@xxx" mentions recursively
 */
  private String recur(String content, int startIndex, StringBuilder sb, Set<Long> mentionedIds) {
    int indexOfAt = content.indexOf('@', startIndex);
    int indexOfSpace = content.indexOf(' ', indexOfAt);
    if (indexOfSpace < 0) {
      indexOfSpace = content.length();
    }
    indexOfAt = content.lastIndexOf('@', indexOfSpace - 1);

    if (indexOfAt >= startIndex && indexOfSpace >= startIndex) {
      String name = content.substring(indexOfAt + 1, indexOfSpace);
      User user = findByName.apply(name);

      if (user != null) {
        // A valid mention
        mentionedIds.add(user.getId());
        sb.append(content.substring(startIndex, indexOfAt)).append('@').append(name)
            .append('#').append(user.getId());
        return recur(content, indexOfSpace, sb, mentionedIds);
      } else {
        if (startIndex == 0) {
          return content;
        }
        sb.append(content.substring(startIndex, indexOfSpace));
        return recur(content, indexOfSpace, sb, mentionedIds);
      }
    }

    // Exit
    if (startIndex == 0) {
      return content;
    }
    return sb.append(content.substring(startIndex)).toString();
  }
}
