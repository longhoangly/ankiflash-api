package theflash.flashcard.payload;

import theflash.flashcard.service.impl.card.EnglishBaseCardServiceImpl;
import theflash.flashcard.utils.Translation;

public class Card {

  private String word;

  private String wordType;

  private String phonetic;

  private String example;

  private String pron;

  private String meaning;

  private String image;

  private char tag;

  private String copyRight;

  private String cardContent;

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

  public Card(String word, Translation translation) {
    EnglishBaseCardServiceImpl englishCard = new EnglishBaseCardServiceImpl();
    this.word = word;
    this.cardContent = englishCard.generateFlashcard(word, translation);
    this.wordType = englishCard.getWordType();
    this.phonetic = englishCard.getPhonetic();
    this.example = englishCard.getExample();
    this.pron = englishCard.getPron();
    this.meaning = englishCard.getMeaning();
    this.image = englishCard.getImage();
    this.tag = englishCard.getTag();
    this.copyRight = englishCard.getCopyRight();
    this.cardContent = englishCard.getCardContent();
  }
}
