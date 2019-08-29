package ankiflash.counter.payload;

import javax.validation.constraints.NotNull;

public class CounterResponse {

  @NotNull private int clientCount;

  @NotNull private int visitCount;

  @NotNull private int cardCount;

  @NotNull private int langCount;

  public CounterResponse(int clientCount, int visitCount, int cardCount, int langCount) {
    this.clientCount = clientCount;
    this.visitCount = visitCount;
    this.cardCount = cardCount;
    this.langCount = langCount;
  }

  public int getClientCount() {
    return clientCount;
  }

  public void setClientCount(int clientCount) {
    this.clientCount = clientCount;
  }

  public int getVisitCount() {
    return visitCount;
  }

  public void setVisitCount(int visitCount) {
    this.visitCount = visitCount;
  }

  public int getCardCount() {
    return cardCount;
  }

  public void setCardCount(int cardCount) {
    this.cardCount = cardCount;
  }

  public int getLangCount() {
    return langCount;
  }

  public void setLangCount(int langCount) {
    this.langCount = langCount;
  }
}
