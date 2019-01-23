package theflash.security.service;

import java.util.Collection;
import theflash.security.dto.User;

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
}
