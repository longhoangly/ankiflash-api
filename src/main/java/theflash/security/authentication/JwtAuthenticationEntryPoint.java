package theflash.security.authentication;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest arg0, HttpServletResponse arg1,
      AuthenticationException arg2) throws IOException {
    arg1.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHARIZED");
  }
}
