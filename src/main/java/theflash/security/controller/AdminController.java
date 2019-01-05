package theflash.security.controller;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import theflash.helper.exception.BadRequestException;
import theflash.security.dto.User;
import theflash.security.payload.SignUpUserRequest;
import theflash.security.payload.SignUpUserResponse;
import theflash.security.service.UserService;
import theflash.security.utils.PassEncoding;
import theflash.security.utils.Roles;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

  private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

  @Autowired
  private UserService userService;

  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping("/create")
  public ResponseEntity create(@RequestBody @Valid SignUpUserRequest reqUser) {

    logger.info("/api/admin/create");

    User user = userService.findByUsername(reqUser.getUsername());
    if (user != null) {
      throw new BadRequestException("Username exists already!");
    }

    user = userService.findByEmail(reqUser.getEmail());
    if (user != null) {
      throw new BadRequestException("Email exists already!");
    }

    user = new User(reqUser.getUsername());
    user.setEmail(reqUser.getEmail());
    user.setPassword(PassEncoding.getInstance().passwordEncoder.encode(reqUser.getPassword()));
    user.setRole(Roles.ROLE_ADMIN.getValue());

    Date now = Calendar.getInstance().getTime();
    user.setCreatedDate(now);
    user.setLastLogin(now);
    user.setActive(true);
    userService.save(user);

    SignUpUserResponse resUser = new SignUpUserResponse(user.getUsername(), user.getRole(),
        user.isActive());
    return ResponseEntity.ok().body(resUser);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping("/get-all-users")
  public ResponseEntity getAllUsers() {

    logger.info("/api/admin/get-all-users");

    Collection<User> users = userService.findAll();
    return ResponseEntity.ok().body(users);
  }
}
