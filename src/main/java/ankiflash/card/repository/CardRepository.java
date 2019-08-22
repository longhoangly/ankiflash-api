package ankiflash.card.repository;

import ankiflash.card.dto.Card;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends CrudRepository<Card, Integer> {

  Card findByHash(String hash);
}
