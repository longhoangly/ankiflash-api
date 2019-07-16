package ankiflash.security.payload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class LoginResponse {

  @NotNull
  @NotEmpty
  private String username;

  @NotNull
  @NotEmpty
  private String role;

  @NotNull
  @NotEmpty
  private boolean active;

  @NotNull
  @NotEmpty
  private boolean verified;

  @NotNull
  @NotEmpty
  private String token;

  public LoginResponse(String username, String role, boolean active, boolean verified, String token) {
    this.username = username;
    this.role = role;
    this.active = active;
    this.verified = verified;
    this.token = token;
  }

  public void setUsername(String userName) {
    this.username = userName;
  }

  public String getUsername() {
    return username;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getRole() {
    return role;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public boolean isVerified() {
    return verified;
  }

  public void setVerified(boolean verified) {
    this.verified = verified;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}