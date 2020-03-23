package transformation.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import transformation.domain.entity.Batch;
import transformation.service.TransformationService;
import transformation.service.archiveCreator.ZIPArchiveCreatorService;
import transformation.service.archiveUnpacker.ZIPArchiveUnpackerService;

import java.net.URI;


@RestController
public class TransformationController {

    private final TransformationService transformationService;

    public TransformationController(TransformationService transformationService) {
        this.transformationService = transformationService;
    }

    @PostMapping("/batches")
    public ResponseEntity<URI> takeAndHandleAndSaveDataToDB(@RequestBody byte[] bytes){
        URI uri = transformationService.handleRequestBodyData(bytes); // method handles request and return URI to saved file
        return ResponseEntity.ok(uri);
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
