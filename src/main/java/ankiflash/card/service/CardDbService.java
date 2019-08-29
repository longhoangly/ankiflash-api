package ankiflash.card.service;

import ankiflash.card.dto.Card;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface CardDbService {

  Card save(Card card);

  Card update(Card card);

  Boolean delete(int id);

  List<Card> findByWord(String word);

  Card findByHash(String hash);

  Collection<Card> findAll();

  int cardCount();
}
