package ankiflash.counter.service.impl;

import ankiflash.card.service.CardStorageService;
import ankiflash.counter.dto.Counter;
import ankiflash.counter.payload.CounterResponse;
import ankiflash.counter.repository.CounterRepository;
import ankiflash.counter.service.CounterService;
import ankiflash.security.service.UserService;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CounterServiceImpl implements CounterService {

  @Autowired private CounterRepository counterRepository;

  @Autowired private CardStorageService cardStorageService;

  @Autowired private UserService userService;

  @Override
  public Counter save(Counter counter) {
    return counterRepository.save(counter);
  }

  @Override
  public Counter update(Counter counter) {
    return counterRepository.save(counter);
  }

  @Override
  public CounterResponse getCounter() {
    Optional<Counter> counterOptional = counterRepository.findById(1);
    Counter counter = counterOptional.orElse(null);
    return counter == null
        ? null
        : new CounterResponse(
            userService.userCount(),
            counter.getVisitCount(),
            cardStorageService.cardCount(),
            counter.getLangCount());
  }

  @Override
  public Counter getDbCounter() {
    Optional<Counter> counterOptional = counterRepository.findById(1);
    return counterOptional.orElse(null);
  }

  @Override
  public void addVisit() {
    Optional<Counter> counterOptional = counterRepository.findById(1);
    Counter counter = counterOptional.orElse(null);
    if (counter != null) {
      counter.setVisitCount(counter.getVisitCount() + 1);
      counterRepository.save(counter);
    }
  }
}
