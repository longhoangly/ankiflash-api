package theflash.utility;

import java.util.Calendar;
import java.util.Date;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import theflash.counter.dto.Counter;
import theflash.counter.service.CounterService;
import theflash.security.dto.User;
import theflash.security.service.UserService;
import theflash.security.utility.PassEncoding;

@Component
public class EntityLoader {

  @Autowired
  private UserService userService;

  @Autowired
  private CounterService counterService;

  @PostConstruct
  public void initUserData() {

    User user = userService.findByUsername("hoanglongtc7");
    if (user == null) {
      user = new User();
      user.setUsername("hoanglongtc7");
      user.setEmail("hoanglongtc7@gmail.com");
      user.setPassword(PassEncoding.getInstance().passwordEncoder.encode("password"));
      user.setVerified(true);
      user.setActive(true);

      Date now = Calendar.getInstance().getTime();
      user.setCreatedDate(now);
      user.setLastLogin(now);
      user.setRole("ADMIN");
      user.setId(1);
      user.setToken(null);
      userService.save(user);
    }
  }

  @PostConstruct
  public void initCounterData() {

    Counter counter = counterService.get();
    if (counter == null) {
      counter = new Counter();
      counter.setId(1);
      counter.setCustomer(userService.countUser());
      counter.setVisit(100);
      counter.setCard(1000);
      counter.setCounter4(5);
      counterService.save(counter);
    }
  }
}
