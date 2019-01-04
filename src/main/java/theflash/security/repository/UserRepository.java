package theflash.security.repository;

import java.util.Date;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import theflash.security.dto.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

  User findByUsername(String username);

  User findByEmail(String email);

  @Modifying
  @Query("Update User set lastLogin = :lastLogin where username = :username")
  void updateLastLogin(@Param("lastLogin") Date lastLogin, @Param("username") String username);
}