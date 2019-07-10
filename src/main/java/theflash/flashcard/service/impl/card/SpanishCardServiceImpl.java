package theflash.flashcard.service.impl.card;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import theflash.flashcard.dto.Card;
import theflash.flashcard.utility.Translation;

public class SpanishCardServiceImpl extends CardServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(SpanishCardServiceImpl.class);

  @Override
  public Card generateCard(String word, Translation translation, String username) {

    logger.info("Word = " + word);
    logger.info("Source = " + translation.getSource());
    logger.info("Target = " + translation.getTarget());

    Card card = new Card(word);

    return card;
  }
}
