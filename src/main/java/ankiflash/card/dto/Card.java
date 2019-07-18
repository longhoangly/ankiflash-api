package ankiflash.card.dto;

import ankiflash.card.utility.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Card {

  private final String word;

  private String wordType;

  private String phonetic;

  private String example;

  private String pron;

  private String meaning;

  private String image;

  private char tag;

  private String copyright;

  @JsonIgnore
  private String content;

  private Status Status;

  private String comment;

  public Card(String word) {
    this.word = word;
  }

  public String getWord() {
    return word;
  }

  public String getWordType() {
    return wordType;
  }

  public void setWordType(String wordType) {
    this.wordType = wordType;
  }

  public String getPhonetic() {
    return phonetic;
  }

  public void setPhonetic(String phonetic) {
    this.phonetic = phonetic;
  }

  public String getExample() {
    return example;
  }

  public void setExample(String example) {
    this.example = example;
  }

  public String getPron() {
    return pron;
  }

  public void setPron(String pron) {
    this.pron = pron;
  }

  public String getMeaning() {
    return meaning;
  }

  public void setMeaning(String meaning) {
    this.meaning = meaning;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public char getTag() {
    return tag;
  }

  public void setTag(char tag) {
    this.tag = tag;
  }

  public String getCopyright() {
    return copyright;
  }

  public void setCopyright(String copyright) {
    this.copyright = copyright;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Status getStatus() {
    return Status;
  }

  public void setStatus(Status status) {
    Status = status;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
}
