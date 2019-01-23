package theflash.counter.service.impl;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import theflash.counter.dto.Counter;
import theflash.counter.repository.CounterRepository;
import theflash.counter.service.CounterService;

@Service
@Transactional
public class CounterServiceImpl implements CounterService {

  @Autowired
  private CounterRepository counterRepository;

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
    return counterRepository.findById(id).get();
  }

  @Override
  public Counter get() {
    return counterRepository.findById(1).get();
  }

  @Override
  public void addCustomer() {
    Counter counter = counterRepository.findById(1).get();
    counter.setCustomer(counter.getCustomer() + 1);
    counterRepository.save(counter);
  }

  @Override
  public void addVisit() {
    Counter counter = counterRepository.findById(1).get();
    counter.setVisit(counter.getVisit() + 1);
    counterRepository.save(counter);
  }

  @Override
  public void addCard() {
    Counter counter = counterRepository.findById(1).get();
    counter.setCard(counter.getCard() + 1);
    counterRepository.save(counter);
  }
}
