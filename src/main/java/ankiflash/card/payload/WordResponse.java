package ankiflash.card.payload;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;

public class WordResponse {

  @NotNull private List<String> success;

  @NotNull private List<String> failure;

  public WordResponse() {
    this.success = new ArrayList<>();
    this.failure = new ArrayList<>();
  }

  public List<String> getSuccess() {
    return success;
  }

  public void setSuccess(List<String> success) {
    this.success = success;
  }

  public List<String> getFailure() {
    return failure;
  }

  public void setFailure(List<String> failure) {
    this.failure = failure;
  }
}
