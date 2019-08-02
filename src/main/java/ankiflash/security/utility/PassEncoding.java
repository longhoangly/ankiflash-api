package ankiflash.security.utility;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PassEncoding {

  private static final PassEncoding passEncoding = new PassEncoding();

  public final BCryptPasswordEncoder passwordEncoder;

  public static PassEncoding getInstance() {
    if (passEncoding != null) {
      return passEncoding;
    }
    return new PassEncoding();
  }

  private PassEncoding() {
    passwordEncoder = new BCryptPasswordEncoder();
  }
}
