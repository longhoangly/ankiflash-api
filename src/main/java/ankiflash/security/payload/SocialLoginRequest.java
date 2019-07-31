package ankiflash.security.payload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class SocialLoginRequest {

  @NotNull
  @NotEmpty
  private String idTokenString;

  public SocialLoginRequest() {
  }

  public String getIdTokenString() {
    return idTokenString;
  }

  public void setIdTokenString(String idTokenString) {
    this.idTokenString = idTokenString;
  }
}
