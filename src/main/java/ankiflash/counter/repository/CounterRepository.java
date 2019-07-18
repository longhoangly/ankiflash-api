package ankiflash.counter.repository;

import ankiflash.counter.dto.Counter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CounterRepository extends CrudRepository<Counter, Integer> {
}
