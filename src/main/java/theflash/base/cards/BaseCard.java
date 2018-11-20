package theflash.base.cards;

import theflash.base.utility.Translation;

public abstract class BaseCard
{
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