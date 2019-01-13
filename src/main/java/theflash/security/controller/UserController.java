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
import theflash.helper.exception.BadRequestException;
import theflash.security.dto.User;
import theflash.security.payload.DeleteUserResponse;
import theflash.security.payload.SignUpUserRequest;
import theflash.security.payload.SignUpUserResponse;
import theflash.security.service.UserService;
import theflash.security.utils.PassEncoding;
import theflash.security.utils.Roles;

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
    return ResponseEntity.ok().body(currentUser);
  }

  @PostMapping("/update")
  public ResponseEntity update(@RequestBody @Valid SignUpUserRequest reqUser) {

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
    reqUser.setPassword(PassEncoding.getInstance().passwordEncoder.encode(reqUser.getPassword()));
    reqUser.setRole(Roles.ROLE_USER.getValue());

    Date now = Calendar.getInstance().getTime();
    user.setCreatedDate(now);
    user.setLastLogin(now);
    user.setActive(true);

    user = userService.update(user);

    SignUpUserResponse resUser = new SignUpUserResponse(user.getUsername(), user.getRole(), user.isActive(),
        user.isVerified());
    return ResponseEntity.ok().body(resUser);
  }

  @PostMapping("/deActive")
  public ResponseEntity deActive() {

    logger.info("/api/user/deActive");

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User user = userService.findByUsername(auth.getName());
    user.setActive(false);
    userService.update(user);

    SignUpUserResponse resUser = new SignUpUserResponse(user.getUsername(), user.getRole(), user.isActive(),
        user.isVerified());
    return ResponseEntity.ok().body(resUser);
  }

  @PostMapping("/delete")
  public ResponseEntity delete() {

    logger.info("/api/user/delete");

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User user = userService.findByUsername(auth.getName());
    userService.delete(user.getId());

    DeleteUserResponse resUser = new DeleteUserResponse(user.getUsername(), user.getRole(),
        true);
    return ResponseEntity.ok().body(resUser);
  }
}
