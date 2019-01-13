package theflash.security.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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
import theflash.helper.exception.BadRequestException;
import theflash.helper.exception.InactiveException;
import theflash.security.dto.User;
import theflash.security.jwt.Generator;
import theflash.security.jwt.Validation;
import theflash.security.payload.ForgotPasswordRequest;
import theflash.security.payload.LoginUserRequest;
import theflash.security.payload.LoginUserResponse;
import theflash.security.payload.SignUpUserRequest;
import theflash.security.payload.SignUpUserResponse;
import theflash.security.service.EmailService;
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

  @Autowired
  private EmailService emailService;

  @Autowired
  private Validation validator;

  @PostMapping("/login")
  public ResponseEntity login(@RequestBody @Valid LoginUserRequest reqUser) {

    logger.info("/api/auth/login");

    User user = userService.validate(reqUser.getUsername(), reqUser.getPassword());
    if (user == null) {
      throw new BadRequestException("Username or Password is not correct!");
    }

    if (!user.isVerified()) {
      throw new InactiveException("Your email was not verified yet! Please check email for verification!");
    }

    if (!user.isActive()) {
      throw new InactiveException("Your account was disabled! Please reset password to enable it!");
    }

    LoginUserResponse resUser = new LoginUserResponse(user.getUsername(), user.getRole(), user.isActive(),
        generator.generate(user));
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
    user.setVerified(false);
    user.setActive(false);
    userService.save(user);

    emailService.sendVerificationEmail(user);
    SignUpUserResponse resUser = new SignUpUserResponse(user.getUsername(), user.getRole(), user.isActive(),
        user.isVerified());
    return ResponseEntity.ok().body(resUser);
  }

  @PostMapping("/request-email-verification-link")
  public ResponseEntity requestEmailVerificationLink(@RequestParam(value = "email") String email) {

    logger.info("/request-email-verification-link");

    User user = userService.findByEmail(email);
    if (user == null) {
      throw new BadRequestException("Email not found!");
    }

    emailService.sendVerificationEmail(user);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/verify-email-address")
  public ResponseEntity verifyEmailAddress(@RequestParam(value = "key") String token) {

    logger.info("/api/auth/verify-email-address");

    User user;
    try {
      user = validator.validate(token);
    } catch (JwtException ex) {
      throw new BadRequestException("Link expired! Please request to get a new link!");
    }

    user.setVerified(true);
    user.setActive(true);
    userService.save(user);

    SignUpUserResponse resUser = new SignUpUserResponse(user.getUsername(), user.getRole(), user.isActive(),
        user.isVerified());
    return ResponseEntity.ok().body(resUser);
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
  public ResponseEntity resetPassword(@RequestBody @Valid ForgotPasswordRequest reqUser) {

    logger.info("/api/auth/reset-password");

    String token = reqUser.getKey();
    User user;
    try {
      user = validator.validate(token);
    } catch (ExpiredJwtException e1) {
      throw new BadRequestException("Link expired! Please select 'Forgot Password' to get a new link!");
    } catch (Exception e2) {
      throw new BadRequestException("Invalid link! Please select 'Forgot Password' to get a new link!");
    }

    if (!reqUser.getPassword().equals(reqUser.getConfirmedPassword())) {
      throw new BadRequestException(
          "Password* and Confirmed Password* do not match! Please request to get a new link!");
    }

    user.setPassword(PassEncoding.getInstance().passwordEncoder.encode(reqUser.getPassword()));
    userService.save(user);

    SignUpUserResponse resUser = new SignUpUserResponse(user.getUsername(), user.getRole(), user.isActive(),
        user.isVerified());
    return ResponseEntity.ok().body(resUser);
  }
}
