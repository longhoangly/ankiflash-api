package ankiflash.utility;

import ankiflash.counter.dto.Counter;
import ankiflash.counter.service.CounterService;
import ankiflash.security.dto.User;
import ankiflash.security.service.UserService;
import ankiflash.security.utility.PassEncoding;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class EntityLoader {

  @Autowired private UserService userService;

  @Autowired private CounterService counterService;

  @PostConstruct
  public void initUserData() {

    User user = userService.findByUsername("hoanglongtc7");
    if (user == null) {
      user = new User();
      user.setUsername("hoanglongtc7");
      user.setEmail("hoanglongtc7@gmail.com");
      user.setPassword(PassEncoding.getInstance().passwordEncoder.encode("longlee@10"));
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

    Counter counter = counterService.getDbCounter();
    if (counter == null) {
      counter = new Counter();
      counter.setId(1);
      counter.setVisitCount(100);
      counter.setLangCount(5);
      counterService.save(counter);
    }
  }
}
