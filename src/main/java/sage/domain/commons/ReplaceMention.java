package sage.domain.commons;

import java.util.Set;

import sage.domain.repository.UserRepository;
import sage.entity.User;

public class ReplaceMention {
  private UserRepository userRepo;

  public static ReplaceMention with(UserRepository userRepo) {
    ReplaceMention instance = new ReplaceMention();
    instance.userRepo = userRepo;
    return instance;
  }

  public String apply(String content, Set<Long> mentionedIds) {
    return recur(content, 0, new StringBuilder(), mentionedIds);
  }

  /*
 * Replace "@xxx" mentions recursively
 */
  private String recur(String content, int startIndex, StringBuilder sb, Set<Long> mentionedIds) {
    int indexOfAt = content.indexOf('@', startIndex);
    int indexOfSpace = content.indexOf(' ', indexOfAt);
    int indexOfInnerAt = content.lastIndexOf('@', indexOfSpace - 1);

    if (indexOfAt >= 0 && indexOfSpace >= 0) {
      if (indexOfInnerAt > indexOfAt && indexOfInnerAt < indexOfSpace) {
        indexOfAt = indexOfInnerAt;
      }
      String name = content.substring(indexOfAt + 1, indexOfSpace);
      User user = userRepo.findByName(name);

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
        sb.append(content.substring(indexOfAt, indexOfSpace));
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
