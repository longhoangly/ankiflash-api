package theflash.security.jwt;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import theflash.security.dto.JwtAuthToken;
import theflash.security.dto.User;
import theflash.security.dto.UserDetail;

@Component
public class AuthProvider extends AbstractUserDetailsAuthenticationProvider {

  @Autowired
  private Validation validator;

  @Override
  protected void additionalAuthenticationChecks(UserDetails userDetails,
      UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
    // TODO Auto-generated method stub
  }

  @Override
  protected UserDetails retrieveUser(String username,
      UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

    JwtAuthToken jwtAuthenticationToken = (JwtAuthToken) authentication;
    String token = jwtAuthenticationToken.getToken();
    User jwtUser = validator.validate(token);

    if (jwtUser == null) {
      throw new AuthenticationServiceException("JWT Token is incorrect");
    }

    List<GrantedAuthority> grantedAuthorities = AuthorityUtils
        .commaSeparatedStringToAuthorityList(jwtUser.getRole());
    return new UserDetail(jwtUser.getUsername(), jwtUser.getPassword(), grantedAuthorities);
  }

  @Override
  public boolean supports(Class<?> aClass) {
    return (JwtAuthToken.class.isAssignableFrom(aClass));
  }
}
