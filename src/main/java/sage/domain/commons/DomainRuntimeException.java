package sage.domain.commons;

public class DomainRuntimeException extends RuntimeException {
  public DomainRuntimeException() {
  }

  public DomainRuntimeException(String message) {
    super(message);
  }

  public DomainRuntimeException(String format, Object... args) {
    this(String.format(format, args));
  }

  public DomainRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public DomainRuntimeException(Throwable cause) {
    super(cause);
  }
}
