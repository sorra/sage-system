package sage.domain.commons;

public class DomainException extends RuntimeException {
  public DomainException() {
  }

  public DomainException(String message) {
    super(message);
  }

  public DomainException(String format, Object... args) {
    this(String.format(format, args));
  }

  public DomainException(String message, Throwable cause) {
    super(message, cause);
  }

  public DomainException(Throwable cause) {
    super(cause);
  }
}
