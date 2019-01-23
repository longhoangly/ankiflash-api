package theflash.security.utility.jwt;

import java.io.IOException;
import java.util.Collection;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import theflash.security.dto.JwtAuthToken;
import theflash.security.dto.User;

public class AuthFilter extends AbstractAuthenticationProcessingFilter {

  @Value("${jwt.token.prefix}")
  private String prefix;

  @Autowired
  private Validation validator;

  public AuthFilter(RequestMatcher matcher) {
    super(matcher);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {

    String header = request.getHeader("Authorization");

    if (header == null || !header.startsWith(prefix)) {
      throw new AuthenticationServiceException("Token is missing");
    }

    String authenticationToken = header.replaceFirst(prefix, "").trim();
    User jwtUser = validator.validate(authenticationToken);
    Collection<GrantedAuthority> grantedAuthorities = AuthorityUtils
        .commaSeparatedStringToAuthorityList(jwtUser.getRole());

    JwtAuthToken token = new JwtAuthToken(authenticationToken, grantedAuthorities);
    return getAuthenticationManager().authenticate(token);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws IOException, ServletException {
    super.successfulAuthentication(request, response, chain, authResult);
    chain.doFilter(request, response);
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed)
      throws IOException, ServletException {
    super.unsuccessfulAuthentication(request, response, failed);
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
  }
}
