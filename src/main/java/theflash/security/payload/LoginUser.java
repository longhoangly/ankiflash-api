package theflash.security.payload;

import java.util.Objects;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import theflash.security.utils.Roles;

public class LoginUser {

  @NotNull
  @NotEmpty
  private String username;

  @NotNull
  @NotEmpty
  private String password;

  @NotNull
  @NotEmpty
  private String role = Roles.ROLE_USER.getValue();

  public LoginUser() {
  }

  public LoginUser(String username, String password, String role) {
    this.setUsername(username);
    this.setPassword(password);
    this.setRole(role);
  }

  public void setUsername(String userName) {
    this.username = userName;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getRole() {
    return role;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LoginUser user = (LoginUser) o;
    return role == user.role &&
        Objects.equals(username, user.username) &&
        Objects.equals(password, user.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, password, role);
  }
}