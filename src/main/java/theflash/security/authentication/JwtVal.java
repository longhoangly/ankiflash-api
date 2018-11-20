package theflash.security.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;
import theflash.security.model.User;

@Component
public class JwtVal {

  private String secret = "fenerbahce";

  public User validate(String token) {

    User jwtUser = null;
    try {
      Claims body = Jwts.parser()
          .setSigningKey(secret)
          .parseClaimsJws(token)
          .getBody();

      jwtUser = new User();

      jwtUser.setUserName(body.getSubject());
      jwtUser.setId(Long.parseLong((String) body.get("userId")));
      jwtUser.setRole((String) body.get("role"));
    } catch (Exception e) {
      System.out.println(e);
    }

    return jwtUser;
  }
}
