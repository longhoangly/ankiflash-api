package theflash.security.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import theflash.security.payload.User;

@Component
public class JwtVal {

  private static final Logger logger = LoggerFactory.getLogger(JwtVal.class);

  @Value("${jwt.token.secret}")
  private String secret;

  public User validate(String token) {
    User jwtUser = null;
    try {
      Claims body = Jwts.parser()
                        .setSigningKey(secret)
                        .parseClaimsJws(token)
                        .getBody();
      jwtUser = new User();
      jwtUser.setUsername(body.getSubject());
      jwtUser.setId(Long.parseLong((String) body.get("userId")));
      jwtUser.setRole((String) body.get("role"));
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
    return jwtUser;
  }
}
