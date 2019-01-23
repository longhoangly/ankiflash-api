package theflash.counter.service;

import theflash.counter.dto.Counter;

public interface CounterService {

  Counter save(Counter counter);

  Counter update(Counter counter);

  Counter get(int id);

  Counter get();

  void addCustomer();

  void addVisit();

  void addCard();
}
