package ankiflash.security.service;

import ankiflash.security.dto.User;
import java.util.Collection;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

  User save(User user);

  Boolean delete(int id);

  User update(User user);

  User findById(int id);

  User findByUsername(String username);

  User findByEmail(String email);

  Collection<User> findAll();

  User validate(String username, String password);

  int countUser();

  String getCurrentUsername();
}
