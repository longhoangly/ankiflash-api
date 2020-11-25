package ankiflash.card.service;

import ankiflash.card.dto.Card;
import ankiflash.card.utility.Translation;
import java.util.List;

public interface CardGeneratingService {

  List<String> getWords(String word, Translation translation);

  Card generateCard(String word, Translation translation, String ankiDir, boolean isOffline);

  List<Card> generateCards(
      List<String> wordList, Translation translation, String ankiDir, boolean isOffline);

  void compressResources(String ankiDir);

  List<Translation> getSupportedLanguages();
}
