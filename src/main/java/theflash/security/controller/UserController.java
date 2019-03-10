package theflash.security.controller;

import java.util.Calendar;
import java.util.Date;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import theflash.utility.exception.BadRequestException;
import theflash.security.dto.User;
import theflash.security.payload.SignUpRequest;
import theflash.security.service.UserService;
import theflash.security.utility.PassEncoding;
import theflash.security.utility.Roles;

@RestController
@RequestMapping("/api/user")
public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private UserService userService;

  @GetMapping("/current")
  public ResponseEntity getCurrentUser() {

    logger.info("/api/user/current");

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User currentUser = userService.findByUsername(auth.getName());
    if (currentUser == null) {
      throw new BadRequestException("Cannot find current user info!");
    }

    return ResponseEntity.ok().body(currentUser);
  }

  @PostMapping("/update")
  public ResponseEntity update(@RequestBody @Valid SignUpRequest reqUser) {

    logger.info("/api/user/update");

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
    user.setLastLogin(now);
    user.setActive(true);
    user.setVerified(true);
    user.setRole(Roles.ROLE_USER.getValue());

    userService.update(user);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/de-active")
  public ResponseEntity deActive() {

    logger.info("/api/user/de-active");

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User user = userService.findByUsername(auth.getName());
    if (user != null) {
      throw new BadRequestException(String.format("User %1$s does not exist!", auth.getName()));
    }

    user.setActive(false);
    userService.update(user);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/delete")
  public ResponseEntity delete() {

    logger.info("/api/user/delete");

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User user = userService.findByUsername(auth.getName());
    if (user != null) {
      throw new BadRequestException(String.format("User %1$s does not exist!", auth.getName()));
    }

    userService.delete(user.getId());
    return ResponseEntity.ok().build();
  }
}
