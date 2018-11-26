package theflash.flashcard.service;

import theflash.flashcard.utils.Translation;

public interface CardService {

  String getWord();

  String getWordType();

  String getPhonetic();

  String getExample();

  String getPron();

  String getMeaning();

  String getImage();

  char getTag();

  String getCopyRight();

  String getCardContent();

  String generateFlashcard(String word, Translation translation);
}
