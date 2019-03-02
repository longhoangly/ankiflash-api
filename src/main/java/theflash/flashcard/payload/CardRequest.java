package theflash.flashcard.payload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class CardRequest {

  @NotNull
  @NotEmpty
  private String wordList;

  @NotNull
  @NotEmpty
  private String source;

  @NotNull
  @NotEmpty
  private String target;

  public CardRequest(String source, String target){
    this.source = source;
    this.target = target;
  }

  public String getWordList() {
    return wordList;
  }

  public void setWordList(String wordList) {
    this.wordList = wordList;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }
}
