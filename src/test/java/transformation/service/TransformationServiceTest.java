package transformation.service;

import org.junit.jupiter.api.Test;
import transformation.repositories.BatchRepository;
import transformation.repositories.ItemRepository;

import static org.junit.jupiter.api.Assertions.*;

class TransformationServiceTest {

    BatchRepository batchRepository;
    ItemRepository itemRepository;

    TransformationService transformationService;

    @Test
    void getBatches() {

        transformationService = new TransformationService(itemRepository, batchRepository);
        System.out.println("sadawda");
        System.out.println(transformationService.getBatches(0,50));
        System.out.println("Adawawd");
    }
}