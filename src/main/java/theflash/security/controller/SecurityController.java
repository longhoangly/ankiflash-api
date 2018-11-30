package theflash.security.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import theflash.security.authentication.JwtGen;
import theflash.security.payload.User;
import theflash.security.service.UserService;
import theflash.security.utils.PassEncoding;
import theflash.security.utils.Roles;

@RestController
@RequestMapping("/api/auth")
public class SecurityController {

  private static final Logger logger = LoggerFactory.getLogger(SecurityController.class);

  @Autowired private UserService userService;

  @Autowired private JwtGen generator;

  @PostMapping("/token")
  public ResponseEntity generate(@RequestBody final User user) {

    logger.info("/api/auth/token");
    HashMap response = new HashMap();
    if (userService.login(user.getUsername(), user.getPassword()) != null) {
      response.put("accessToken", generator.generate(user));
    } else {
      response.put("error", "username or password is not correct");
    }
    return new ResponseEntity(response, HttpStatus.OK);
  }

  @PostMapping("/register")
  public ResponseEntity register(@RequestBody final User reqUser) {

    logger.info("/api/auth/register");
    User user = userService.findByUserName(reqUser.getUsername());
    if (user != null) {
      HashMap response = new HashMap();
      response.put("error", "username exists already");
      return new ResponseEntity(response, HttpStatus.OK);
    }

    user = userService.findByEmail(reqUser.getEmail());
    if (user != null) {
      HashMap response = new HashMap();
      response.put("error", "email exists already");
      return new ResponseEntity(response, HttpStatus.OK);
    }

    reqUser.setPassword(PassEncoding.getInstance().passwordEncoder.encode(reqUser.getPassword()));
    reqUser.setRole(Roles.ROLE_USER.getValue());

    Date now = Calendar.getInstance().getTime();
    reqUser.setCreatedDate(now);
    reqUser.setLastLogin(now);

    user = userService.save(reqUser);
    user.setPassword("**********");
    return new ResponseEntity(user, HttpStatus.OK);
  }
}
