package theflash.security.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import theflash.security.payload.User;

@Component
public class JwtGen {

  public String generate(User jwtUser) {
    Claims claims = Jwts.claims().setSubject(jwtUser.getUsername());
    claims.put("userId", String.valueOf(jwtUser.getId()));
    claims.put("role", jwtUser.getRole());
    return Jwts.builder()
               .setClaims(claims)
               .signWith(SignatureAlgorithm.HS512, "26e9e1476c86c6939ca29a8046f01a9d")
               .compact();
  }
}
