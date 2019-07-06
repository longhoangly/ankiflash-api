package theflash.flashcard.service;

import java.util.List;
import org.springframework.stereotype.Service;
import theflash.flashcard.dto.Card;
import theflash.flashcard.utility.Translation;

@Service
public interface CardService {

  Card generateCard(String word, Translation translation, String username);

  List<Card> generateCards(List<String> wordList, Translation translation, String username);

  String compressResources(String username);

  List<Translation> getSupportedLanguages();
}
