package ankiflash.security.service.impl;

import ankiflash.counter.service.CounterService;
import ankiflash.security.dto.User;
import ankiflash.security.service.SocialAuthService;
import ankiflash.security.service.UserService;
import ankiflash.security.utility.Roles;
import ankiflash.utility.JsonUtility;
import ankiflash.utility.exception.ErrorHandler;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

  @Autowired private UserService userService;

  @Autowired private CounterService counterService;

  NetHttpTransport transport = new NetHttpTransport();
  GsonFactory jsonFactory = new GsonFactory();

  GoogleIdTokenVerifier verifier =
      new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
          .setAudience(
              Collections.singletonList(
                  "956431695223-po5d2ahv7mejsm84hl911vg03696k9n8.apps.googleusercontent.com"))
          .build();

  @Override
  public User googleVerify(String idTokenString) {

    try {
      GoogleIdToken idToken = verifier.verify(idTokenString);
      if (idToken != null) {

        Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        logger.info("email: {}", email);

        User userByEmail = userService.findByEmail(email);
        if (userByEmail == null) {

          String userId = payload.getSubject();
          logger.info("userId: {}", userId);

          String name = (String) payload.get("name");
          logger.info("name: {}", name);

          User user = new User(userId);
          user.setPassword("***");
          user.setEmail(email);

          Date now = Calendar.getInstance().getTime();
          user.setCreatedDate(now);
          user.setLastLogin(now);
          user.setActive(true);
          user.setVerified(true);
          user.setRole(Roles.ROLE_USER.getValue());

          userService.save(user);
          return user;
        } else {
          return userByEmail;
        }
      } else {
        logger.warn("Invalid ID token.");
      }
    } catch (GeneralSecurityException | IOException e) {
      ErrorHandler.log(e);
    }
    return null;
  }

  @Override
  public User facebookVerify(String accessToken) {

    String body = String.format("access_token=%1$s&fields=id,name,email", accessToken);
    JsonObject meJson = JsonUtility.postRequest("https://graph.facebook.com/v3.3/me", body);
    JsonElement facebookId = meJson.get("id");
    if (facebookId != null && !facebookId.getAsString().isEmpty()) {

      String userId = facebookId.getAsString();
      String email = meJson.get("email").getAsString();
      logger.info("userId: {}", userId);
      logger.info("email: {}", email);

      User userById = userService.findByUsername(userId);
      User userByEmail = userService.findByEmail(email);
      if (userById == null && userByEmail == null) {

        String name = meJson.get("name").getAsString();
        logger.info("name: {}", name);

        User user = new User(userId);
        user.setPassword("***");
        if (email != null && !email.isEmpty()) {
          user.setEmail(email);
        } else {
          user.setEmail(userId + "@facebook.com");
        }

        Date now = Calendar.getInstance().getTime();
        user.setCreatedDate(now);
        user.setLastLogin(now);
        user.setActive(true);
        user.setVerified(true);
        user.setRole(Roles.ROLE_USER.getValue());

        userService.save(user);
        return user;
      } else {
        return userById != null ? userById : userByEmail;
      }
    } else {
      logger.warn("Invalid access token.");
    }
    return null;
  }
}
