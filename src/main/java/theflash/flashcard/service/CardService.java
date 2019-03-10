package theflash.flashcard.service;

import java.util.List;
import theflash.flashcard.dto.Card;
import theflash.flashcard.utils.Translation;

public interface CardService {

  List<Translation> supportedTranslations();

  Card generateCard(String word, Translation translation, String username);

  List<Card> generateCards(List<String> wordList, Translation translation, String username);

  String compressResources(String username);
}
