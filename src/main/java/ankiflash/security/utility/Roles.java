package ankiflash.security.utility;

public enum Roles {
  ROLE_ADMIN("ADMIN"),
  ROLE_USER("USER");
  private String value;

  Roles(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
