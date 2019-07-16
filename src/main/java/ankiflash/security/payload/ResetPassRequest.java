package ankiflash.security.payload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ResetPassRequest {

  @NotNull
  @NotEmpty
  private String key;

  @NotNull
  @NotEmpty
  private String password;

  public ResetPassRequest() {
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
