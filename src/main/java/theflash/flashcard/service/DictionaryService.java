package theflash.flashcard.service;

import org.springframework.stereotype.Service;
import theflash.flashcard.utility.Translation;

@Service
public interface DictionaryService {

  boolean isConnectionEstablished(String word, Translation translation);

  boolean isWordingCorrect();

  String getWordType();

  String getExample();

  String getPhonetic();

  String getImage(String username, String selector);

  String getPron(String username, String selector);

  String getMeaning();

  char getTag();

  String getDictionaryName();
}
