package ankiflash.card.service.impl;

import ankiflash.card.dto.Card;
import ankiflash.card.repository.CardRepository;
import ankiflash.card.service.CardStorageService;
import ankiflash.utility.exception.ErrorHandler;
import java.util.Collection;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CardStorageServiceImpl implements CardStorageService {

  @Autowired private CardRepository cardRepository;

  @Override
  public Card save(Card card) {
    try {
      return cardRepository.save(card);
    } catch (Exception e) {
      ErrorHandler.log(e);
    }
    return null;
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
  public List<Card> findByWord(String word) {
    return cardRepository.findByWord(word);
  }

  @Override
  public Card findByHash(String hash) {
    return cardRepository.findByHash(hash);
  }

  @Override
  public Collection<Card> findAll() {
    return (Collection<Card>) cardRepository.findAll();
  }

  @Override
  public int cardCount() {
    return Math.toIntExact(cardRepository.count());
  }
}
