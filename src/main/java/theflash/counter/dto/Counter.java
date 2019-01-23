package theflash.counter.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "counter")
public class Counter {

  @Id
  @Column(name = "id", unique = true)
  private int id;

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

  public Counter() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
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
