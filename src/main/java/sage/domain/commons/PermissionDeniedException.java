package sage.domain.commons;

public class PermissionDeniedException extends DomainException {
  public PermissionDeniedException(String message) {
    super(message);
  }

  public PermissionDeniedException(String format, Object... args) {
    super(format, args);
  }

  public PermissionDeniedException(String message, Throwable cause) {
    super(message, cause);
  }
}
