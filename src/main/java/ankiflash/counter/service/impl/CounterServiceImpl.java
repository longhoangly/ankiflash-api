package ankiflash.counter.service.impl;

import ankiflash.counter.dto.Counter;
import ankiflash.counter.repository.CounterRepository;
import ankiflash.counter.service.CounterService;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CounterServiceImpl implements CounterService {

  @Autowired private CounterRepository counterRepository;

  @Override
  public Counter save(Counter counter) {
    return counterRepository.save(counter);
  }

  @Override
  public Counter update(Counter counter) {
    return counterRepository.save(counter);
  }

  @Override
  public Counter get(int id) {
    Optional<Counter> counter = counterRepository.findById(id);
    return counter.orElse(null);
  }

  @Override
  public Counter get() {
    Optional<Counter> counter = counterRepository.findById(1);
    return counter.orElse(null);
  }

  @Override
  public void addCustomer() {
    // noinspection OptionalGetWithoutIsPresent
    Counter counter = counterRepository.findById(1).get();
    counter.setCustomer(counter.getCustomer() + 1);
    counterRepository.save(counter);
  }

  @Override
  public void addVisit() {
    // noinspection OptionalGetWithoutIsPresent
    Counter counter = counterRepository.findById(1).get();
    counter.setVisit(counter.getVisit() + 1);
    counterRepository.save(counter);
  }

  @Override
  public void addCard() {
    // noinspection OptionalGetWithoutIsPresent
    Counter counter = counterRepository.findById(1).get();
    counter.setCard(counter.getCard() + 1);
    counterRepository.save(counter);
  }
}
