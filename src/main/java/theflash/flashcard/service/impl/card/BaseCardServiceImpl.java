package theflash.flashcard.service.impl.card;

import theflash.flashcard.service.card.CardService;
import theflash.flashcard.utils.Translation;

public abstract class BaseCardServiceImpl implements CardService {

  protected String word;

  protected String wordType;

  protected String phonetic;

  protected String example;

  protected String pron;

  protected String meaning;

  protected String image;

  protected char tag;

  protected String copyRight;

  protected String cardContent;

  public String getWord() {
    return word;
  }

  public String getWordType() {
    return wordType;
  }

  public String getPhonetic() {
    return phonetic;
  }

  public String getExample() {
    return example;
  }

  public String getPron() {
    return pron;
  }

  public String getMeaning() {
    return meaning;
  }

  public String getImage() {
    return image;
  }

  public char getTag() {
    return tag;
  }

  public String getCopyRight() {
    return copyRight;
  }

  public String getCardContent() {
    return cardContent;
  }

  public abstract String generateFlashcard(String word, Translation translation);
}