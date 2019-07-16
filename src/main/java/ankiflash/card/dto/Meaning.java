package ankiflash.card.dto;

import java.util.List;

public class Meaning {

  private String wordType;

  private String meaning;

  private List<String> examples;

  public Meaning() {
  }

  public Meaning(String meaning, List<String> examples) {
    this.meaning = meaning;
    this.examples = examples;
  }

  public String getWordType() {
    return wordType;
  }

  public void setWordType(String wordType) {
    this.wordType = wordType;
  }

  public String getMeaning() {
    return meaning;
  }

  public void setMeaning(String meaning) {
    this.meaning = meaning;
  }

  public List<String> getExamples() {
    return examples;
  }

  public void setExamples(List<String> examples) {
    this.examples = examples;
  }
}
