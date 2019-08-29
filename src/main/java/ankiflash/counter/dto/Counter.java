package ankiflash.counter.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "counter")
public class Counter {

  @Id
  @JsonIgnore
  @Column(name = "id", unique = true)
  private int id;

  @NotNull
  @Column(name = "visitCount")
  private int visitCount;

  @NotNull
  @Column(name = "langCount")
  private int langCount;

  public Counter() {}

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getVisitCount() {
    return visitCount;
  }

  public void setVisitCount(int visitCount) {
    this.visitCount = visitCount;
  }

  public int getLangCount() {
    return langCount;
  }

  public void setLangCount(int langCount) {
    this.langCount = langCount;
  }
}
