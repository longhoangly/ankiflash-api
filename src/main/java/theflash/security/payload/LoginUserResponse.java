package theflash.security.payload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class LoginUserResponse {

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
  private String token;

  public LoginUserResponse(String username, String role, boolean active, String token) {
    this.username = username;
    this.role = role;
    this.active = active;
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

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}