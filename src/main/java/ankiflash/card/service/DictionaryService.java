package ankiflash.card.service;

import ankiflash.card.utility.Translation;

public interface DictionaryService {

  boolean isConnected(String word, Translation translation);

  boolean isInvalidWord();

  String getWordType();

  String getExample();

  String getPhonetic();

  void getImages(String ankiDir, boolean isOffline);

  String getImageOnline();

  String getImageOffline();

  String getImageLink();

  void getSounds(String ankiDir, boolean isOffline);

  String getSoundOnline();

  String getSoundOffline();

  String getSoundLink();

  String getMeaning();

  String getTag();

  String getDictionaryName();
}
