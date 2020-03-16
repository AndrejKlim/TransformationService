package transformation.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import transformation.domain.entity.Item;

import java.util.List;

public interface ItemRepository extends CrudRepository<Item, Integer> {

    List<Item> findAllByBatch_Id(Integer batch_id, Pageable pageable);

}
