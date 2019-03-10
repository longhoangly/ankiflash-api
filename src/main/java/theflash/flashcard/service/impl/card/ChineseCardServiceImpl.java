package theflash.flashcard.service.impl.card;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import theflash.flashcard.controller.CardController;
import theflash.flashcard.dto.Card;
import theflash.flashcard.utils.Constants;
import theflash.flashcard.utils.Translation;

public class ChineseCardServiceImpl extends CardServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(ChineseCardServiceImpl.class);

  @Override
  public List<Translation> supportedTranslations() {
    List<Translation> translations = new ArrayList<>();
    translations.add(new Translation(Constants.CHINESE, Constants.ENGLISH));
    translations.add(new Translation(Constants.CHINESE, Constants.VIETNAMESE));
    return translations;
  }

  @Override
  public Card generateCard(String word, Translation translation, String username) {
    Card card = new Card(word);

    logger.debug("Word = " + word);
    logger.debug("Translation = " + translation);

    return card;
  }

  @Override
  public List<Card> generateCards(List<String> words, Translation translation, String username) {
    List<Card> cardCollection = new ArrayList<>();
    for (String word : words) {
      cardCollection.add(generateCard(word, translation, username));
    }
    return cardCollection;
  }
}
