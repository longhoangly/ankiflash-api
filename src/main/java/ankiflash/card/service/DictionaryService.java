package ankiflash.card.service;

import ankiflash.card.utility.Translation;

public interface DictionaryService {

  boolean isConnectionFailed(String word, Translation translation);

  boolean isWordNotFound();

  String getWordType();

  String getExample();

  String getPhonetic();

  String getImage(String ankiDir, String selector);

  String getPron(String ankiDir, String selector);

  String getMeaning();

  char getTag();

  String getDictionaryName();
}
