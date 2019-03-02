package theflash.flashcard.service.impl.card;

import java.util.List;
import theflash.flashcard.dto.Card;
import theflash.flashcard.service.CardService;
import theflash.flashcard.utils.Translation;

public abstract class CardServiceImpl implements CardService {

  protected Card card;

  protected List<Card> cardCollection;

  protected String cardLink;

  public abstract Card generateCard(String word, Translation translation);

  public abstract List<Card> generateCards(List<String> wordList, Translation translation);

  public abstract String getDownloadLink();
}