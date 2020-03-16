package transformation.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import transformation.domain.Batch;

import java.util.List;

public interface BatchRepository extends CrudRepository<Batch, Integer> {

    List<Batch> findAll(Pageable pageable);

}
