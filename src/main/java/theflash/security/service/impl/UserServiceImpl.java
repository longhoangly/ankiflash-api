package theflash.security.service.impl;

import java.util.Calendar;
import java.util.Collection;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import theflash.security.dto.User;
import theflash.security.repository.UserRepository;
import theflash.security.service.UserService;
import theflash.security.utility.PassEncoding;

@Service
@Transactional
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public User save(User user) {
    return userRepository.save(user);
  }

  @Override
  public Boolean delete(int id) {
    if (userRepository.existsById(id)) {
      userRepository.deleteById(id);
      return true;
    }
    return false;
  }

  @Override
  public User update(User user) {
    return userRepository.save(user);
  }

  @Override
  public User findById(int id) {
    return userRepository.findById(id).get();
  }

  @Override
  public User findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  public User findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Override
  public Collection<User> findAll() {
    return (Collection<User>) userRepository.findAll();
  }

  @Override
  public User validate(String username, String password) {
    User user = userRepository.findByUsername(username);
    if (user == null) {
      return null;
    } else {
      if (PassEncoding.getInstance().passwordEncoder.matches(password, user.getPassword())) {
        userRepository.updateLastLogin(Calendar.getInstance().getTime(), user.getUsername());
        return user;
      } else {
        return null;
      }
    }
  }

  @Override
  public int countUser() {
    return Math.toIntExact(userRepository.count());
  }

  @Override
  public String getCurrentUsername() {
    // Authenticate User
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User user = findByUsername(auth.getName());
    return user != null ? user.getUsername() : "";
  }
}
