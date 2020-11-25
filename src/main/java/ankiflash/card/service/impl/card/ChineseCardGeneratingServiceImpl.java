package ankiflash.card.service.impl.card;

import ankiflash.card.dto.Card;
import ankiflash.card.utility.Translation;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ChineseCardGeneratingServiceImpl extends CardGeneratingServiceImpl {

  private static final Logger logger =
      LoggerFactory.getLogger(ChineseCardGeneratingServiceImpl.class);

  @Override
  public List<String> getWords(String word, Translation translation) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Card generateCard(
      String combinedWord, Translation translation, String ankiDir, boolean isOffline) {
    throw new UnsupportedOperationException();
  }
}
