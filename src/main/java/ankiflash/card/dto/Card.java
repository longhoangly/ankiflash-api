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
  @JsonIgnore
  @Column(name = "id", unique = true)
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;

  @NotNull
  @NotEmpty
  @JsonIgnore
  @Column(name = "hash", unique = true, columnDefinition = "VARCHAR(500)")
  private String hash;

  @NotNull
  @NotEmpty
  @Column(name = "word")
  private String word;

  @NotNull
  @NotEmpty
  @JsonIgnore
  @Column(name = "wordId")
  private String wordId;

  @NotNull
  @NotEmpty
  @JsonIgnore
  @Column(name = "originalWord")
  private String originalWord;

  @NotNull
  @Column(name = "wordType")
  private String wordType;

  @NotNull
  @Column(name = "phonetic")
  private String phonetic;

  @NotNull
  @Column(name = "example", columnDefinition = "LONGTEXT")
  private String example;

  @NotNull
  @NotEmpty
  @Column(name = "imageOffline")
  private String imageOffline;

  @NotNull
  @NotEmpty
  @Column(name = "imageOnline")
  private String imageOnline;

  @NotNull
  @JsonIgnore
  @Column(name = "imageLink")
  private String imageLink;

  @NotNull
  @JsonIgnore
  @Column(name = "imageName")
  private String imageName;

  @NotNull
  @Column(name = "soundOffline")
  private String soundOffline;

  @NotNull
  @Column(name = "soundOnline")
  private String soundOnline;

  @NotNull
  @JsonIgnore
  @Column(name = "soundLink")
  private String soundLink;

  @NotNull
  @JsonIgnore
  @Column(name = "soundName")
  private String soundName;

  @NotNull
  @NotEmpty
  @Column(name = "meaning", columnDefinition = "LONGTEXT")
  private String meaning;

  @NotNull
  @Column(name = "tag")
  private String tag;

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

  @NotNull
  @NotEmpty
  @JsonIgnore
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
    return hash.equals(card.hash) && word.equals(card.word);
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

  public String getImageOffline() {
    return imageOffline;
  }

  public void setImageOffline(String imageOffline) {
    this.imageOffline = imageOffline;
  }

  public String getSoundOffline() {
    return soundOffline;
  }

  public void setSoundOffline(String soundOffline) {
    this.soundOffline = soundOffline;
  }

  public String getImageLink() {
    return imageLink;
  }

  public void setImageLink(String imageLink) {
    this.imageLink = imageLink;
  }

  public String getSoundLink() {
    return soundLink;
  }

  public void setSoundLink(String soundLink) {
    this.soundLink = soundLink;
  }

  public String getImageOnline() {
    return imageOnline;
  }

  public void setImageOnline(String imageOnline) {
    this.imageOnline = imageOnline;
  }

  public String getImageName() {
    return imageName;
  }

  public void setImageName(String imageName) {
    this.imageName = imageName;
  }

  public String getSoundOnline() {
    return soundOnline;
  }

  public void setSoundOnline(String soundOnline) {
    this.soundOnline = soundOnline;
  }

  public String getSoundName() {
    return soundName;
  }

  public void setSoundName(String soundName) {
    this.soundName = soundName;
  }

  public String getMeaning() {
    return meaning;
  }

  public void setMeaning(String meaning) {
    this.meaning = meaning;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public String getCopyright() {
    return copyright;
  }

  public void setCopyright(String copyright) {
    this.copyright = copyright;
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
