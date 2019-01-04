package theflash.security.payload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class DeleteUserResponse {

  @NotNull
  @NotEmpty
  private String username;

  @NotNull
  @NotEmpty
  private String role;

  @NotNull
  @NotEmpty
  private boolean deleted;

  public DeleteUserResponse(String username, String role, boolean deleted) {
    this.username = username;
    this.role = role;
    this.deleted = deleted;
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

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }
}
