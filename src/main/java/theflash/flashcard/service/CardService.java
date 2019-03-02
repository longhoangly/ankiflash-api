package theflash.flashcard.service;

import java.util.List;
import theflash.flashcard.dto.Card;
import theflash.flashcard.utils.Translation;

public interface CardService {

  Card generateCard(String word, Translation translation);

  List<Card> generateCards(List<String> wordList, Translation translation);

  String getDownloadLink();
}
