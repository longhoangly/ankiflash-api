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
import theflash.security.dto.User;
import theflash.security.jwt.Generator;
import theflash.security.payload.LoginUserRequest;
import theflash.security.payload.LoginUserResponse;
import theflash.security.payload.SignUpUserRequest;
import theflash.security.payload.SignUpUserResponse;
import theflash.security.service.UserService;
import theflash.security.utils.PassEncoding;
import theflash.security.utils.Roles;

@RestController
@RequestMapping("/api/auth")
public class SecurityController {

  private static final Logger logger = LoggerFactory.getLogger(SecurityController.class);

  @Autowired
  private UserService userService;

  @Autowired
  private Generator generator;

  @PostMapping("/login")
  public ResponseEntity login(@RequestBody @Valid LoginUserRequest reqUser) {

    logger.info("/api/auth/login");

    User user = userService.validate(reqUser.getUsername(), reqUser.getPassword());
    if (user == null) {
      throw new BadRequestException("Username or Password is not correct!");
    }

    LoginUserResponse resUser = new LoginUserResponse(user.getUsername(), user.getRole(), user.isActive(), generator.generate(user));
    return ResponseEntity.ok().body(resUser);
  }

  @PostMapping("/register")
  public ResponseEntity register(@RequestBody @Valid SignUpUserRequest reqUser) {

    logger.info("/api/auth/register");

    User user = userService.findByUsername(reqUser.getUsername());
    if (user != null) {
      throw new BadRequestException("Username exists already");
    }

    user = userService.findByEmail(reqUser.getEmail());
    if (user != null) {
      throw new BadRequestException("Email exists already");
    }

    user = new User(reqUser.getUsername());
    user.setEmail(reqUser.getEmail());
    user.setPassword(PassEncoding.getInstance().passwordEncoder.encode(reqUser.getPassword()));
    user.setRole(Roles.ROLE_USER.getValue());

    Date now = Calendar.getInstance().getTime();
    user.setCreatedDate(now);
    user.setLastLogin(now);
    user.setActive(true);
    userService.save(user);

    SignUpUserResponse resUser = new SignUpUserResponse(user.getUsername(), user.getRole(),
        user.isActive());
    return ResponseEntity.ok().body(resUser);
  }
}
