package ankiflash.counter.service;

import org.springframework.stereotype.Service;
import ankiflash.counter.dto.Counter;

@Service
public interface CounterService {

  Counter save(Counter counter);

  Counter update(Counter counter);

  Counter get(int id);

  Counter get();

  void addCustomer();

  void addVisit();

  void addCard();
}
