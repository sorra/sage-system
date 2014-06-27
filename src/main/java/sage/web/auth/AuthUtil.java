package sage.web.auth;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriUtils;

import sage.web.context.WebContexts;

public class AuthUtil {
  private final static Logger logger = LoggerFactory.getLogger(AuthUtil.class);

  public static Long checkCurrentUid() {
    Long cuid = currentUid();
    if (cuid == null) {
      logger.debug("require login");
      throw new RequireLoginException();
    }
    else
      return cuid;
  }

  public static Long currentUid() {
    return (Long) WebContexts.getSessionAttr(SessionKeys.UID);
  }

  public static void invalidateSession(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }
  }

  static String getRedirectGoto(String requestLink) {
    try {
      return "goto=" + UriUtils.encodeQueryParam(requestLink, "ISO-8859-1");
    }
    catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  static String decodeLink(String link) {
    try {
      return UriUtils.decode(link, "ISO-8859-1");
    }
    catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }
}
