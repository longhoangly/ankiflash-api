package theflash.security.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import theflash.security.payload.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

  User findByUsername(String username);

  User findByEmail(String email);
}