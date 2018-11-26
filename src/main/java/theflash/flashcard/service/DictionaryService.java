package theflash.flashcard.service;

import java.util.List;
import theflash.flashcard.utils.Translation;

public interface DictionaryService {
  
  List<Translation> supportedTranslations();

  boolean isConnectionEstablished(String word, Translation translation);

  boolean isWordingCorrect();

  String getWordType();

  String getExample();

  String getPhonetic();

  String getImage(String selector, String attr);

  String getPron(String selector);

  String getMeaning();

  char getTag();

  String getDictionaryName();
}
