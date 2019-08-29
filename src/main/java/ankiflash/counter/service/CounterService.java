package ankiflash.counter.service;

import ankiflash.counter.dto.Counter;
import ankiflash.counter.payload.CounterResponse;
import org.springframework.stereotype.Service;

@Service
public interface CounterService {

  Counter save(Counter counter);

  Counter update(Counter counter);

  CounterResponse getCounter();

  Counter getDbCounter();

  void addVisit();
}
