package transformation.repositories;

import org.springframework.data.repository.CrudRepository;
import transformation.domain.Item;

public interface ItemRepository extends CrudRepository<Item, Integer> {
}
