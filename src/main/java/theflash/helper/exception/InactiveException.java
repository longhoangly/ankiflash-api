package theflash.helper.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class InactiveException extends RuntimeException {

  public InactiveException() {
    super();
  }

  public InactiveException(String message, Throwable cause) {
    super(message, cause);
  }

  public InactiveException(String message) {
    super(message);
  }

  public InactiveException(Throwable cause) {
    super(cause);
  }
}
