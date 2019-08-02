package ankiflash.security.payload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class LoginRequest {

  @NotNull @NotEmpty private String username;

  @NotNull @NotEmpty private String password;

  public LoginRequest() {}

  public void setUsername(String userName) {
    this.username = userName;
  }

  public String getUsername() {
    return username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPassword() {
    return password;
  }
}
