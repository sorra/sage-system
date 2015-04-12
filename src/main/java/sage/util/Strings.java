package sage.util;

/**
 * String utilities
 */
public class Strings {

  /**
   * Produce a substring. No matter if source is shorter than endIndex.
   * @param source source string
   * @param beginIndex inclusive
   * @param endIndex exclusive
   * @return
   */
  public static String cut(String source, int beginIndex, int endIndex) {
    if (endIndex < beginIndex) {
      throw new IllegalArgumentException(String.format("begin: %d, end: %d", beginIndex, endIndex));
    }
    if (endIndex > source.length()) {
      return source.substring(beginIndex);
    } else {
      return source.substring(beginIndex, endIndex);
    }
  }

  /**
   * Replace all occurences of a substring within a string with
   * another string.
   *
   * @param inString   String to examine
   * @param oldPattern String to replace
   * @param newPattern String to insert
   * @return a String with the replacements
   */
  // Code from ElasticSearch
  public static String replace(String inString, String oldPattern, String newPattern) {
    if (!hasLength(inString) || !hasLength(oldPattern) || newPattern == null) {
      return inString;
    }
    StringBuilder sb = new StringBuilder();
    int pos = 0; // our position in the old string
    int index = inString.indexOf(oldPattern);
    // the index of an occurrence we've found, or -1
    int patLen = oldPattern.length();
    while (index >= 0) {
      sb.append(inString.substring(pos, index));
      sb.append(newPattern);
      pos = index + patLen;
      index = inString.indexOf(oldPattern, pos);
    }
    sb.append(inString.substring(pos));
    // remember to append any characters to the right of a match
    return sb.toString();
  }

  /**
   * Check that the given String is neither <code>null</code> nor of length 0.
   * Note: Will return <code>true</code> for a String that purely consists of whitespace.
   *
   * @param str the String to check (may be <code>null</code>)
   * @return <code>true</code> if the String is not null and has length
   */
  public static boolean hasLength(CharSequence str) {
    return (str != null && str.length() > 0);
  }
}
