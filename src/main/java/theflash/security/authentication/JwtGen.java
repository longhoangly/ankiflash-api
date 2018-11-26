package theflash.security.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import theflash.security.payload.User;

@Component
public class JwtGen {

  @Value("${jwt.token.expiration}")
  private long expiration;

  @Value("${jwt.token.secret}")
  private String secret;

  @Value("${jwt.token.prefix}")
  private String prefix;

  public String generate(User jwtUser) {
    Claims claims = Jwts.claims().setSubject(jwtUser.getUsername());
    claims.put("userId", String.valueOf(jwtUser.getId()));
    claims.put("role", jwtUser.getRole());

    return prefix + " " + Jwts.builder()
                              .setClaims(claims)
                              .setExpiration(new Date(System.currentTimeMillis() + expiration))
                              .signWith(SignatureAlgorithm.HS512, secret)
                              .compact();
  }
}
