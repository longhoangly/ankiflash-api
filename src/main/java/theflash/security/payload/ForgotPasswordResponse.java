package theflash.security.payload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ForgotPasswordResponse {

  @NotNull
  @NotEmpty
  private String email;

  private boolean success;

  public ForgotPasswordResponse(String email, boolean success) {
    this.email = email;
    this.success = success;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public boolean getSuccess() {
    return success;
  }
}
