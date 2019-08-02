package ankiflash.security.payload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class SocialLoginRequest {

  @NotNull @NotEmpty private String idTokenString;

  @NotNull @NotEmpty private String provider;

  public SocialLoginRequest() {}

  public String getIdTokenString() {
    return idTokenString;
  }

  public void setIdTokenString(String idTokenString) {
    this.idTokenString = idTokenString;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }
}
