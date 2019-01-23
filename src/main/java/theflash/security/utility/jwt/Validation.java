package theflash.security.utility.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import theflash.security.dto.User;
import theflash.security.service.UserService;

@Component
public class Validation {

  @Autowired
  private UserService userService;

  @Value("${jwt.token.secret}")
  private String secret;

  public User validate(String token) {
    Claims body = Jwts.parser()
                      .setSigningKey(secret)
                      .parseClaimsJws(token)
                      .getBody();
    return userService.findByUsername(body.getSubject());
  }
}
