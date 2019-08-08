package ankiflash.card.payload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class CardRequest {

  @NotNull @NotEmpty private String words;

  @NotNull @NotEmpty private String source;

  @NotNull @NotEmpty private String target;

  @NotNull @NotEmpty private String sessionId;

  public CardRequest(String source, String target) {
    this.source = source;
    this.target = target;
  }

  public String getWords() {
    return words;
  }

  public void setWords(String words) {
    this.words = words;
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

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }
}
