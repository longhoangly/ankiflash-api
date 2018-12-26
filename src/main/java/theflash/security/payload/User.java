package theflash.security.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import theflash.security.utils.Roles;

@Entity
@Table(name = "user")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonProperty(access = Access.WRITE_ONLY)
  @Column(name = "id", unique = true)
  private int id;

  @NotNull
  @NotEmpty
  @Column(name = "username")
  private String username;

  @NotNull
  @NotEmpty
  @JsonProperty(access = Access.WRITE_ONLY)
  @Column(name = "password")
  private String password;

  @NotNull
  @NotEmpty
  @JsonProperty(access = Access.WRITE_ONLY)
  @Column(name = "email")
  private String email;

  @JsonProperty(access = Access.WRITE_ONLY)
  @Column(name = "role")
  private String role = Roles.ROLE_USER.getValue();

  private String token;

  @JsonProperty(access = Access.WRITE_ONLY)
  @Column(name = "createdDate")
  private Date createdDate = Calendar.getInstance().getTime();

  @JsonProperty(access = Access.WRITE_ONLY)
  @Column(name = "lastLogin")
  private Date lastLogin = Calendar.getInstance().getTime();

  @Column(name = "active")
  private boolean active = true;

  public User() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return id == user.id &&
        role == user.role &&
        Objects.equals(username, user.username) &&
        Objects.equals(password, user.password) &&
        Objects.equals(email, user.email);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username, password, email, role);
  }

  // Setter and Getter
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Date getLastLogin() {
    return lastLogin;
  }

  public void setLastLogin(Date lastLogin) {
    this.lastLogin = lastLogin;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
