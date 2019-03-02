package theflash.flashcard.utils;

import java.util.List;

public class Meaning {

  private String meaning;

  private List<String> examples;

  public Meaning(String meaning, List<String> examples) {
    this.meaning = meaning;
    this.examples = examples;
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
