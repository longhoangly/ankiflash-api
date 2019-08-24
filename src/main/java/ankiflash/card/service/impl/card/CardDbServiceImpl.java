package ankiflash.card.service.impl.card;

import ankiflash.card.dto.Card;
import ankiflash.card.repository.CardRepository;
import ankiflash.card.service.CardDbService;
import com.google.api.client.util.Base64;
import java.util.Collection;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CardDbServiceImpl implements CardDbService {

  @Autowired private CardRepository cardRepository;

  @Override
  public Card save(Card card) {
    return cardRepository.save(card);
  }

  @Override
  public Card update(Card card) {
    return cardRepository.save(card);
  }

  @Override
  public Boolean delete(int id) {
    if (cardRepository.existsById(id)) {
      cardRepository.deleteById(id);
      return true;
    }
    return false;
  }

  @Override
  public Card findById(int id) {
    return cardRepository.findById(id).get();
  }

  @Override
  public List<Card> findByWord(String word) {
    return cardRepository.findByWord(word);
  }

  @Override
  public Card findByHash(String combinedWord) {
    String hash = new String(Base64.encodeBase64(combinedWord.getBytes()));
    return cardRepository.findByHash(hash);
  }

  @Override
  public Collection<Card> findAll() {
    return (Collection<Card>) cardRepository.findAll();
  }

  @Override
  public int countCard() {
    return Math.toIntExact(cardRepository.count());
  }
}
