package theflash.security.payload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ForgotPasswordRequest {

  @NotNull
  @NotEmpty
  private String email;

  private String username;

  public ForgotPasswordRequest() {
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setUsername(String userName) {
    this.username = userName;
  }

  public String getUsername() {
    return username;
  }
}
