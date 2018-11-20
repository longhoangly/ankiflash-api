package theflash.security.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import theflash.security.model.User;

@Component
public class JwtGen {

  public String generate(User jwtUser) {

    Claims claims = Jwts.claims()
        .setSubject(jwtUser.getUserName());
    claims.put("userId", String.valueOf(jwtUser.getId()));
    claims.put("role", jwtUser.getRole());

    return Jwts.builder()
        .setClaims(claims)
        .signWith(SignatureAlgorithm.HS512, "fenerbahce")
        .compact();
  }
}
