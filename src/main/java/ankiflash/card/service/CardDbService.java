package ankiflash.card.service;

import ankiflash.card.dto.Card;
import java.util.Collection;
import org.springframework.stereotype.Service;

@Service
public interface CardDbService {

  Card save(Card card);

  Card update(Card card);

  Boolean delete(int id);

  Card findById(int id);

  Card findByHash(String hash);

  Collection<Card> findAll();

  int countCard();
}
