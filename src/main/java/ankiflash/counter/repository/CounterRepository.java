package ankiflash.counter.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ankiflash.counter.dto.Counter;

@Repository
public interface CounterRepository extends CrudRepository<Counter, Integer> {
}
