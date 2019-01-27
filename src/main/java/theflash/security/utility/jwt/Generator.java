package theflash.security.utility.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import theflash.security.dto.User;

@Component
public class Generator {

  @Value("${jwt.token.expiration.seconds}")
  private long expirationInSeconds;

  @Value("${jwt.token.secret}")
  private String secret;

  @Value("${jwt.token.prefix}")
  private String prefix;

  public String generate(User jwtUser) {
    Claims claims = Jwts.claims().setSubject(jwtUser.getUsername());
    return Jwts.builder()
               .setClaims(claims)
               .setExpiration(new Date(System.currentTimeMillis() + expirationInSeconds*1000))
               .signWith(SignatureAlgorithm.HS512, secret)
               .compact();
  }

  public String generate(User jwtUser, int expirationInSeconds) {
    Claims claims = Jwts.claims().setSubject(jwtUser.getUsername());
    return Jwts.builder()
               .setClaims(claims)
               .setExpiration(new Date(System.currentTimeMillis() + expirationInSeconds*1000))
               .signWith(SignatureAlgorithm.HS512, secret)
               .compact();
  }
}