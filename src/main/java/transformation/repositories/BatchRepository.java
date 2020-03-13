package transformation.repositories;

import org.springframework.data.repository.CrudRepository;
import transformation.domain.Batch;

public interface BatchRepository extends CrudRepository<Batch, Integer> {
}
