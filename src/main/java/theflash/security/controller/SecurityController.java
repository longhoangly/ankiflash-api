package theflash.security.controller;

import java.util.Calendar;
import java.util.Date;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import theflash.counter.service.CounterService;
import theflash.utility.TheFlashProperties;
import theflash.utility.exception.BadRequestException;
import theflash.security.dto.User;
import theflash.security.utility.jwt.Generator;
import theflash.security.utility.jwt.Validation;
import theflash.security.payload.LoginRequest;
import theflash.security.payload.LoginResponse;
import theflash.security.payload.ResetPassRequest;
import theflash.security.payload.SignUpRequest;
import theflash.security.service.EmailService;
import theflash.security.service.UserService;
import theflash.security.utility.PassEncoding;
import theflash.security.utility.Roles;

@RestController
@RequestMapping("/api/auth")
public class SecurityController {

  private static final Logger logger = LoggerFactory.getLogger(SecurityController.class);

  @Autowired
  private UserService userService;

  @Autowired
  private EmailService emailService;

  @Autowired
  private CounterService counterService;

  @Autowired
  private Generator generator;

  @Autowired
  private Validation validator;

  @PostMapping("/login")
  public ResponseEntity login(@RequestBody @Valid LoginRequest reqUser) {

    logger.info("/api/auth/login");

    User user = userService.validate(reqUser.getUsername(), reqUser.getPassword());
    if (user == null) {
      throw new BadRequestException("Username or Password is not correct!");
    }

    LoginResponse resUser = new LoginResponse(user.getUsername(), user.getRole(), user.isActive(),
        user.isVerified(), null);
    if (!user.isVerified()) {
      return ResponseEntity.ok().body(resUser);
    }

    if (!user.isActive()) {
      return ResponseEntity.ok().body(resUser);
    }

    resUser.setToken(generator.generate(user));
    return ResponseEntity.ok().body(resUser);
  }

  @PostMapping("/register")
  public ResponseEntity register(@RequestBody @Valid SignUpRequest reqUser) {

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
    user.setPassword(PassEncoding.getInstance().passwordEncoder.encode(reqUser.getPassword()));
    user.setEmail(reqUser.getEmail());

    Date now = Calendar.getInstance().getTime();
    user.setCreatedDate(now);
    user.setLastLogin(now);
    user.setActive(true);
    user.setVerified(false);
    user.setRole(Roles.ROLE_USER.getValue());

    userService.save(user);
    emailService.sendVerificationEmail(user);

    counterService.addCustomer();
    LoginResponse resUser = new LoginResponse(user.getUsername(), user.getRole(), user.isActive(),
        user.isVerified(), null);
    return ResponseEntity.ok().body(resUser);
  }

  @GetMapping("/request-verify-email-address-link")
  public ResponseEntity requestVerifyEmailink(@RequestParam(value = "email") String email) {

    logger.info("/request-verify-email-address-link");

    User user = userService.findByEmail(email);
    if (user == null) {
      throw new BadRequestException("Email not found!");
    }

    emailService.sendVerificationEmail(user);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/verify-email-address")
  public ModelAndView verifyEmailAddress(@RequestParam(value = "key") String token) {

    logger.info("/api/auth/verify-email-address");

    User user;
    try {
      user = validator.validate(token);
    } catch (Exception e) {
      logger.error("Exception: ", e);
      throw new BadRequestException(
          "Oops! Something's wrong, please select 'Forgot Password' on 'Login' page to try again!");
    }

    if (!user.getToken().equals(token)) {
      return new ModelAndView("redirect:" + TheFlashProperties.WEB_SERVER_URL +
          "/status?code=400&message=Key not found, please select 'Forgot Password' on 'Login' page to try again!");
    }

    user.setActive(true);
    user.setVerified(true);
    userService.save(user);

    return new ModelAndView("redirect:" + TheFlashProperties.WEB_SERVER_URL +
        "/status?code=200&message=Email verified successfully!");
  }

  @GetMapping("/request-reset-password-link")
  public ResponseEntity requestResetPasswordLink(@RequestParam(value = "email") String email) {

    logger.info("/api/auth/reset-password");

    User user = userService.findByEmail(email);
    if (user == null) {
      throw new BadRequestException("Email not found!");
    }

    emailService.sendResetPasswordEmail(user);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/reset-password")
  public ResponseEntity resetPassword(@RequestBody @Valid ResetPassRequest reqUser) {

    logger.info("/api/auth/reset-password");

    String token = reqUser.getKey();
    User user;
    try {
      user = validator.validate(token);
    } catch (Exception e) {
      logger.error("Exception: ", e);
      throw new BadRequestException(
          "Oops! Something's wrong, please select 'Forgot Password' on 'Login' page to try again!");
    }

    if (!user.getToken().equals(token)) {
      throw new BadRequestException("Key not found, please select 'Forgot Password' on 'Login' page to try again!");
    }

    user.setPassword(PassEncoding.getInstance().passwordEncoder.encode(reqUser.getPassword()));
    user.setActive(true);
    userService.save(user);
    return ResponseEntity.ok().build();
  }
}
