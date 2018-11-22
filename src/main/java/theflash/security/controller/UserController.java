package theflash.security.controller;

import java.util.HashMap;
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
@RequestMapping("/user")
public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private UserService userService;

  private User currentUser;

  @GetMapping("/current")
  public ResponseEntity<User> getCurrentUser() {
    logger.info("/current");
    if (currentUser == null) {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      currentUser = userService.findByUserName(auth.getName());
    }

    return new ResponseEntity<>(currentUser, HttpStatus.OK);
  }

  @PostMapping("/register")
  public ResponseEntity<Object> register(@RequestBody final User reqUser) {
    logger.info("/register");
    User user = userService.findByUserName(reqUser.getUsername());
    if (user != null) {
      HashMap response = new HashMap<String, String>();
      response.put("Error","Username exists already");
      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    user = userService.findByEmail(reqUser.getEmail());
    if (user != null) {
      HashMap response = new HashMap<String, String>();
      response.put("Error","Email exists already");
      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    reqUser.setPassword(PassEncoding.getInstance().passwordEncoder.encode(reqUser.getPassword()));
    reqUser.setRole(Roles.ROLE_USER.getValue());

    user = userService.save(reqUser);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }
}
