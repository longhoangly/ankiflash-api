package ankiflash.card.dto;

import ankiflash.card.utility.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.api.client.util.Base64;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "card")
public class Card {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", unique = true)
  private int id;

  @NotNull
  @NotEmpty
  @Column(name = "hash", unique = true)
  private String hash;

  @NotNull
  @NotEmpty
  @Column(name = "word")
  private String word;

  @NotNull
  @NotEmpty
  @Column(name = "wordId")
  private String wordId;

  @NotNull
  @NotEmpty
  @Column(name = "originalWord")
  private String originalWord;

  @NotNull
  @NotEmpty
  @Column(name = "wordType")
  private String wordType;

  @NotNull
  @NotEmpty
  @Column(name = "phonetic")
  private String phonetic;

  @NotNull
  @NotEmpty
  @Column(name = "example", columnDefinition = "LONGTEXT")
  private String example;

  @NotNull
  @NotEmpty
  @Column(name = "pron")
  private String pron;

  @NotNull
  @NotEmpty
  @Column(name = "meaning", columnDefinition = "LONGTEXT")
  private String meaning;

  @NotNull
  @NotEmpty
  @Column(name = "image")
  private String image;

  @NotNull
  @Column(name = "tag")
  private char tag;

  @NotNull
  @NotEmpty
  @Column(name = "copyright")
  private String copyright;

  @NotNull
  @Column(name = "status")
  private Status status;

  @NotNull
  @NotEmpty
  @Column(name = "comment")
  private String comment;

  @JsonIgnore
  @Column(name = "content", columnDefinition = "LONGTEXT")
  private String content;

  @NotNull
  @NotEmpty
  @Column(name = "translation")
  private String translation;

  public Card() {}

  public Card(String word, String wordId, String originalWord, String translation) {
    this.word = word;
    this.wordId = wordId;
    this.originalWord = originalWord;
    this.translation = translation;
    this.hash =
        new String(
            Base64.encodeBase64(
                (this.word + ":" + this.wordId + ":" + this.originalWord + ":" + this.translation)
                    .getBytes()));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Card card = (Card) o;
    return hash == card.hash && word.equals(card.word);
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public String getWord() {
    return word;
  }

  public void setWord(String word) {
    this.word = word;
  }

  public String getWordId() {
    return wordId;
  }

  public void setWordId(String wordId) {
    this.wordId = wordId;
  }

  public String getOriginalWord() {
    return originalWord;
  }

  public void setOriginalWord(String originalWord) {
    this.originalWord = originalWord;
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
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getTranslation() {
    return translation;
  }

  public void setTranslation(String translation) {
    this.translation = translation;
  }
}
