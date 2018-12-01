package theflash.security.controller;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/admin")
public class AdminController {

  private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

  @Autowired private UserService userService;

  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping("/create")
  public ResponseEntity create(@RequestBody @Valid User reqUser) {

    logger.info("/api/admin/create");
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
    reqUser.setRole(Roles.ROLE_ADMIN.getValue());

    Date now = Calendar.getInstance().getTime();
    reqUser.setCreatedDate(now);
    reqUser.setLastLogin(now);
    reqUser.setActive(true);

    user = userService.save(reqUser);
    user.setPassword("**********");
    return new ResponseEntity(user, HttpStatus.OK);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping("/get-all-users")
  public ResponseEntity getAllUsers() {

    logger.info("/api/admin/get-all-users");
    Collection<User> users = userService.findAll();
    return new ResponseEntity(users, HttpStatus.OK);
  }
}
