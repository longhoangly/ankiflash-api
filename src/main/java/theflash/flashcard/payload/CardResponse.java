package theflash.flashcard.payload;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import theflash.flashcard.dto.Card;

public class CardResponse {

  @NotNull
  private List<Card> cardCollection;

  public CardResponse() {
    this.cardCollection = new ArrayList<>();
  }

  public void addCard(Card card) {
    this.cardCollection.add(card);
  }

  public List<Card> getCardCollection() {
    return cardCollection;
  }

  public void setCardCollection(List<Card> cardCollection) {
    this.cardCollection = cardCollection;
  }
}
