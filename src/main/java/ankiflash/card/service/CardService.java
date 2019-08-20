package ankiflash.card.service;

import ankiflash.card.dto.Card;
import ankiflash.card.utility.Translation;
import java.util.List;

public interface CardService {

  List<String> getWords(String word, Translation translation);

  Card generateCard(String word, Translation translation, String ankiDir);

  List<Card> generateCards(List<String> wordList, Translation translation, String ankiDir);

  String compressResources(String ankiDir);

  List<Translation> getSupportedLanguages();
}
