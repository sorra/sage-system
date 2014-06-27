package sage.web.auth;

public class RequireLoginException extends RuntimeException {
  private static final long serialVersionUID = -7612510838405643774L;

  public RequireLoginException() {
    super();
  }

  public RequireLoginException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public RequireLoginException(String message, Throwable cause) {
    super(message, cause);
  }

  public RequireLoginException(String message) {
    super(message);
  }

  public RequireLoginException(Throwable cause) {
    super(cause);
  }

}
