package ankiflash.card.service.impl.card;

import ankiflash.card.dto.Card;
import ankiflash.card.utility.Translation;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChineseCardServiceImpl extends CardServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(ChineseCardServiceImpl.class);

  @Override
  public List<String> getWords(String word, Translation translation) {

    List<String> engWords = new ArrayList<>();
    engWords.add(word);
    return engWords;
  }

  @Override
  public Card generateCard(String word, Translation translation, String ankiDir) {

    logger.info("Word = " + word);
    logger.info("Source = " + translation.getSource());
    logger.info("Target = " + translation.getTarget());

    Card card = new Card(word);

    return card;
  }
}
