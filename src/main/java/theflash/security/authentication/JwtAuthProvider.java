package theflash.security.authentication;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import theflash.security.model.JwtAuthToken;
import theflash.security.model.User;
import theflash.security.model.UserDetail;

@Component
public class JwtAuthProvider extends AbstractUserDetailsAuthenticationProvider {

  @Autowired
  private JwtVal validator;

  @Override
  protected void additionalAuthenticationChecks(UserDetails userDetails,
      UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
    // TODO Auto-generated method stub

  }

  @Override
  protected UserDetails retrieveUser(String username,
      UsernamePasswordAuthenticationToken authentication)
      throws AuthenticationException {
    JwtAuthToken jwtAuthenticationToken = (JwtAuthToken) authentication;
    String token = jwtAuthenticationToken.getToken();

    User jwtUser = validator.validate(token);

    if (jwtUser == null) {
      throw new RuntimeException("JWT Token is incorrect");
    }

    List<GrantedAuthority> grantedAuthorities = AuthorityUtils
        .commaSeparatedStringToAuthorityList(jwtUser.getRole());
    return new UserDetail(jwtUser.getUserName(), jwtUser.getId(), token, grantedAuthorities);
  }

  @Override
  public boolean supports(Class<?> aClass) {
    return (JwtAuthToken.class.isAssignableFrom(aClass));
  }

}
