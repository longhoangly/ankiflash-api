package theflash.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import theflash.security.payload.LoginUser;

@Component
public class Generator {

  @Value("${jwt.token.expiration}")
  private long expiration;

  @Value("${jwt.token.secret}")
  private String secret;

  @Value("${jwt.token.prefix}")
  private String prefix;

  public String generate(LoginUser jwtUser) {
    Claims claims = Jwts.claims().setSubject(jwtUser.getUsername());
    String token = Jwts.builder()
                       .setClaims(claims)
                       .setExpiration(new Date(System.currentTimeMillis() + expiration))
                       .signWith(SignatureAlgorithm.HS512, secret)
                       .compact();
    return String.format("%s %s", prefix, token);
  }
}
