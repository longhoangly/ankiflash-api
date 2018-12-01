package theflash.security.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import theflash.security.payload.User;
import theflash.security.service.UserService;
import theflash.security.utils.PassEncoding;
import theflash.security.utils.Roles;

@RestController
@RequestMapping("/api/user")
public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @Autowired private UserService userService;

  @GetMapping("/current")
  public ResponseEntity getCurrentUser() {

    logger.info("/api/user/current");
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User currentUser = userService.findByUsername(auth.getName());
    return new ResponseEntity(currentUser, HttpStatus.OK);
  }

  @PostMapping("/update")
  public ResponseEntity register(@RequestBody @Valid User reqUser) {

    logger.info("/api/user/update");
    User user = userService.findByUsername(reqUser.getUsername());
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
    reqUser.setActive(true);

    user = userService.update(reqUser);
    user.setPassword("**********");
    return new ResponseEntity(user, HttpStatus.OK);
  }

  @PostMapping("/deActive")
  public ResponseEntity deActive() {

    logger.info("/api/user/deActive");
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User user = userService.findByUsername(auth.getName());
    user.setActive(false);
    userService.update(user);
    user.setPassword("**********");
    return new ResponseEntity(user, HttpStatus.OK);
  }

  @PostMapping("/delete")
  public ResponseEntity delete() {

    logger.info("/api/user/delete");
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User user = userService.findByUsername(auth.getName());
    userService.delete(user.getId());

    HashMap response = new HashMap();
    response.put("username", user.getUsername());
    response.put("status", "deleted!");
    return new ResponseEntity(response, HttpStatus.OK);
  }
}
