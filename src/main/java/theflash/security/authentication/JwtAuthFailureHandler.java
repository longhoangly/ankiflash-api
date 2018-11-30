package theflash.security.authentication;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class JwtAuthFailureHandler implements AuthenticationFailureHandler {

  private static final Logger logger = LoggerFactory.getLogger(JwtAuthFailureHandler.class);

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authentication) throws IOException {
    logger.error("Authentication was failed...");
  }
}
