package theflash.counter.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import theflash.counter.dto.Counter;

@Repository
public interface CounterRepository extends CrudRepository<Counter, Integer> {
}
