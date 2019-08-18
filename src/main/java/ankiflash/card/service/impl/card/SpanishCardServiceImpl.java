package ankiflash.card.service.impl.card;

import ankiflash.card.dto.Card;
import ankiflash.card.utility.Translation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpanishCardServiceImpl extends CardServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(SpanishCardServiceImpl.class);

  @Override
  public Card generateCard(String word, Translation translation, String ankiDir) {

    logger.info("Word = " + word);
    logger.info("Source = " + translation.getSource());
    logger.info("Target = " + translation.getTarget());

    Card card = new Card(word);

    return card;
  }
}
