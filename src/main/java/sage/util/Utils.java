package sage.util;

public class Utils {
  private static final char[] hexCode = "0123456789abcdef".toCharArray();

  public static String toHexString(byte[] data) {
    StringBuilder r = new StringBuilder(data.length * 2);
    for (byte b : data) {
      r.append(hexCode[(b >> 4) & 0xF]);
      r.append(hexCode[(b & 0xF)]);
    }
    return r.toString();
  }

  public static boolean isStaticResource(String uri) {
    return (uri.endsWith(".css") || uri.endsWith(".js")) && uri.startsWith("/static/");
  }
}
