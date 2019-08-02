package ankiflash.security.payload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class SignUpRequest {

  @NotNull @NotEmpty private String username;

  @NotNull @NotEmpty private String password;

  @NotNull @NotEmpty private String email;

  public SignUpRequest() {}

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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
