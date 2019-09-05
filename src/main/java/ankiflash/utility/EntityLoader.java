package ankiflash.utility;

import ankiflash.counter.dto.Counter;
import ankiflash.counter.service.CounterService;
import ankiflash.security.dto.User;
import ankiflash.security.service.UserService;
import ankiflash.security.utility.PassEncoding;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class EntityLoader {

  @Autowired private UserService userService;

  @Autowired private CounterService counterService;

  @Autowired private EntityManagerFactory emf;

  @PostConstruct
  public void initUtf8mb4() {

    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();

    tx.begin();
    em.createNativeQuery("SET NAMES 'utf8mb4' COLLATE 'utf8mb4_unicode_ci;'").executeUpdate();
    tx.commit();

    tx = em.getTransaction();
    tx.begin();
    em.createNativeQuery(
            "ALTER DATABASE ankiflash CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;")
        .executeUpdate();
    tx.commit();

    tx = em.getTransaction();
    tx.begin();
    em.createNativeQuery(
            "ALTER TABLE ankiflash.card MODIFY COLUMN meaning LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;")
        .executeUpdate();
    tx.commit();

    tx = em.getTransaction();
    tx.begin();
    em.createNativeQuery("set global character_set_server=utf8mb4;").executeUpdate();
    tx.commit();

    tx = em.getTransaction();
    tx.begin();
    em.createNativeQuery("set session character_set_server=utf8mb4;").executeUpdate();
    tx.commit();

    tx = em.getTransaction();
    tx.begin();
    em.createNativeQuery("set global character_set_database=utf8mb4;").executeUpdate();
    tx.commit();

    tx = em.getTransaction();
    tx.begin();
    em.createNativeQuery("set session character_set_database=utf8mb4;").executeUpdate();
    tx.commit();

    tx = em.getTransaction();
    tx.begin();
    em.createNativeQuery("set global character_set_client=utf8mb4;").executeUpdate();
    tx.commit();

    tx = em.getTransaction();
    tx.begin();
    em.createNativeQuery("set session character_set_client=utf8mb4;").executeUpdate();
    tx.commit();

    tx = em.getTransaction();
    tx.begin();
    em.createNativeQuery("set global character_set_connection=utf8mb4;").executeUpdate();
    tx.commit();

    tx = em.getTransaction();
    tx.begin();
    em.createNativeQuery("set session character_set_connection=utf8mb4;").executeUpdate();
    tx.commit();

    tx = em.getTransaction();
    tx.begin();
    em.createNativeQuery("set global character_set_system=utf8mb4;").executeUpdate();
    tx.commit();

    tx = em.getTransaction();
    tx.begin();
    em.createNativeQuery("set session character_set_system=utf8mb4;").executeUpdate();
    tx.commit();

    tx = em.getTransaction();
    tx.begin();
    em.createNativeQuery("set global character_set_results=utf8mb4;").executeUpdate();
    tx.commit();

    tx = em.getTransaction();
    tx.begin();
    em.createNativeQuery("set session character_set_results=utf8mb4;").executeUpdate();
    tx.commit();
    em.close();
  }

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
