package theflash.security.controller;

import java.util.Calendar;
import java.util.Date;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import theflash.helper.exception.BadRequestException;
import theflash.security.jwt.Generator;
import theflash.security.payload.LoginUser;
import theflash.security.payload.User;
import theflash.security.service.UserService;
import theflash.security.utils.PassEncoding;
import theflash.security.utils.Roles;

@RestController
@RequestMapping("/api/auth")
public class SecurityController {

  private static final Logger logger = LoggerFactory.getLogger(SecurityController.class);

  @Autowired private UserService userService;

  @Autowired private Generator generator;

  @PostMapping("/login")
  public ResponseEntity login(@RequestBody @Valid LoginUser user) {

    logger.info("/api/auth/login");
    User loggedUser = userService.validate(user.getUsername(), user.getPassword());
    if (loggedUser == null) {
      throw new BadRequestException("Username or Password is not correct!");
    }

    loggedUser.setToken(generator.generate(user));
    return ResponseEntity.ok().body(loggedUser);
  }

  @PostMapping("/register")
  public ResponseEntity register(@RequestBody @Valid User reqUser) {

    logger.info("/api/auth/register");
    User user = userService.findByUsername(reqUser.getUsername());
    if (user != null) {
      throw new BadRequestException("Username exists already");
    }

    user = userService.findByEmail(reqUser.getEmail());
    if (user != null) {
      throw new BadRequestException("Email exists already");
    }

    reqUser.setPassword(PassEncoding.getInstance().passwordEncoder.encode(reqUser.getPassword()));
    reqUser.setRole(Roles.ROLE_USER.getValue());

    Date now = Calendar.getInstance().getTime();
    reqUser.setCreatedDate(now);
    reqUser.setLastLogin(now);
    reqUser.setActive(true);

    user = userService.save(reqUser);
    return ResponseEntity.ok(user);
  }
}
