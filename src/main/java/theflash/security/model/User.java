package theflash.security.model;

public class User {

  private long id;

  private String userName;

  private String role;

  public void setId(long id) {
    this.id = id;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public long getId() {
    return id;
  }

  public String getUserName() {
    return userName;
  }

  public String getRole() {
    return role;
  }
}
