package ankiflash.counter.payload;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

public class CounterResponse {

  @NotNull
  @Column(name = "customer")
  private int customer;

  @NotNull
  @Column(name = "visit")
  private int visit;

  @NotNull
  @Column(name = "card")
  private int card;

  @NotNull
  @Column(name = "counter4")
  private int counter4;

  public CounterResponse(int customer, int visit, int card, int counter4) {
    this.customer = customer;
    this.visit = visit;
    this.card = card;
    this.counter4 = counter4;
  }

  public int getCustomer() {
    return customer;
  }

  public void setCustomer(int customer) {
    this.customer = customer;
  }

  public int getVisit() {
    return visit;
  }

  public void setVisit(int visit) {
    this.visit = visit;
  }

  public int getCard() {
    return card;
  }

  public void setCard(int card) {
    this.card = card;
  }

  public int getCounter4() {
    return counter4;
  }

  public void setCounter4(int counter4) {
    this.counter4 = counter4;
  }
}
