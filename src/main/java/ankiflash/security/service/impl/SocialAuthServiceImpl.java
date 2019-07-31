package ankiflash.security.service.impl;

import ankiflash.counter.service.CounterService;
import ankiflash.security.dto.User;
import ankiflash.security.service.EmailService;
import ankiflash.security.service.SocialAuthService;
import ankiflash.security.service.UserService;
import ankiflash.security.utility.Roles;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SocialAuthServiceImpl implements SocialAuthService {

  private static final Logger logger = LoggerFactory.getLogger(SocialAuthServiceImpl.class);

  @Autowired
  private UserService userService;

  @Autowired
  private CounterService counterService;

  NetHttpTransport transport = new NetHttpTransport();
  GsonFactory jsonFactory = new GsonFactory();
  GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
      .setAudience(Collections
          .singletonList("956431695223-po5d2ahv7mejsm84hl911vg03696k9n8.apps.googleusercontent.com"))
      .build();

  @Override
  public User verify(String idTokenString) {

    try {
      GoogleIdToken idToken = verifier.verify(idTokenString);
      if (idToken != null) {

        Payload payload = idToken.getPayload();
        String userId = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());

        logger.info("userId: " + userId);
        logger.info("name: " + name);
        logger.info("email: " + email);
        logger.info("emailVerified: " + emailVerified);

        User user = userService.findByEmail(email);
        if (user == null) {

          user = new User(email);
          user.setPassword("***");
          user.setEmail(email);

          Date now = Calendar.getInstance().getTime();
          user.setCreatedDate(now);
          user.setLastLogin(now);
          user.setActive(true);
          user.setVerified(true);
          user.setRole(Roles.ROLE_USER.getValue());

          userService.save(user);
          counterService.addCustomer();
        }

        return user;

      } else {
        logger.error("Invalid ID token.");
      }
    } catch (GeneralSecurityException | IOException e) {
      logger.error("Exception Occurred: ", e);
    }

    return null;
  }
}
