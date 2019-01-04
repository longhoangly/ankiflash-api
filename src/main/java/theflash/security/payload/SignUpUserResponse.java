package theflash.security.payload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class SignUpUserResponse {

  @NotNull
  @NotEmpty
  private String username;

  @NotNull
  @NotEmpty
  private String role;

  @NotNull
  @NotEmpty
  private boolean active;

  public SignUpUserResponse(String username, String role, boolean active) {
    this.username = username;
    this.role = role;
    this.active = active;
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
}
