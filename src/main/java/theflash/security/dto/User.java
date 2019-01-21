package theflash.security.dto;

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

@Entity
@Table(name = "user")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", unique = true)
  private int id;

  @NotNull
  @NotEmpty
  @Column(name = "username")
  private String username;

  @NotNull
  @NotEmpty
  @Column(name = "password")
  private String password;

  @NotNull
  @NotEmpty
  @Column(name = "email")
  private String email;

  @NotNull
  @NotEmpty
  @Column(name = "role")
  private String role;

  @NotNull
  @Column(name = "createdDate")
  private Date createdDate;

  @NotNull
  @Column(name = "lastLogin")
  private Date lastLogin;

  @NotNull
  @Column(name = "active")
  private boolean active;

  @NotNull
  @Column(name = "verified")
  private boolean verified;

  @Column(name = "token")
  private String token;

  public User() {
  }

  public User(String username) {
    this.username = username;
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
