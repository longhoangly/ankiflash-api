package ankiflash.card.service;

import ankiflash.card.dto.Card;
import ankiflash.card.utility.Translation;
import java.util.List;

public interface CardService {

  Card generateCard(String word, Translation translation, String username, String sessionId);

  List<Card> generateCards(
      List<String> wordList, Translation translation, String username, String sessionId);

  String compressResources(String username, String sessionId);

  List<Translation> getSupportedLanguages();
}
