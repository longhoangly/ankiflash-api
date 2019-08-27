package ankiflash.card.service;

import ankiflash.card.utility.Translation;

public interface DictionaryService {

  boolean isConnectionFailed(String word, Translation translation);

  boolean isWordNotFound();

  String getWordType();

  String getExample();

  String getPhonetic();

  void preProceedImage(String ankiDir, String selector);

  String getImageOnline();

  String getImageOffline();

  String getImageLink();

  String getImageName();

  void downloadImage();

  void downloadImage(String ankiDir, String imageLink);

  void preProceedSound(String ankiDir, String selector);

  String getSoundOnline();

  String getSoundOffline();

  String getSoundLink();

  String getSoundName();

  void downloadSound();

  void downloadSound(String ankiDir, String imageLink);

  String getMeaning();

  String getTag();

  String getDictionaryName();
}
