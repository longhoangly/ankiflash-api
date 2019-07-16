package ankiflash.card.service;

import java.util.List;
import ankiflash.card.dto.Card;
import ankiflash.card.utility.Translation;

public interface CardService {

  Card generateCard(String word, Translation translation, String username);

  List<Card> generateCards(List<String> wordList, Translation translation, String username);

  String compressResources(String username);

  List<Translation> getSupportedLanguages();
}
