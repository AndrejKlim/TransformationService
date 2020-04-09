package transformation.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import transformation.domain.entity.Batch;
import transformation.service.TransformationService;

import java.util.concurrent.ForkJoinPool;


@RestController
public class TransformationController {

    private static final Logger LOGGER = LogManager.getLogger(TransformationController.class);

    private final TransformationService transformationService;

    public TransformationController(TransformationService transformationService) {
        this.transformationService = transformationService;
    }

    @PostMapping("/batches")
    public ResponseEntity<?> takeAndHandleAndSaveDataToDB(@RequestBody byte[] bytes){

        ForkJoinPool.commonPool().submit(() -> {
            transformationService.handleRequestBodyData(bytes);
            LOGGER.info(String.format("Saving in DB ended in thread %s", Thread.currentThread().getName()));
        });

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/batches")
    public String getListOfAllUploadsOrderedByDate(@RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                                   @RequestParam(value = "limit", defaultValue = "50") Integer limit){

        return transformationService.getBatchesFromDBInJsonOrderedByUploadDate(offset, limit);
    }

    @GetMapping("/batches/{batch}/items")
    public String getBatchContent(@RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                  @RequestParam(value = "limit", defaultValue = "50") Integer limit,
                                  @PathVariable Batch batch){
        return transformationService.getItemsFromDBByBatchInJson(offset, limit, batch);
    }
}
