package transformation.controllers;

import org.springframework.web.bind.annotation.*;
import transformation.domain.ItemList;
import transformation.domain.entity.Batch;
import transformation.domain.BatchList;
import transformation.service.TransformationService;


@RestController
public class TransformationController {

    private final TransformationService transformationService;

    public TransformationController(TransformationService transformationService) {
        this.transformationService = transformationService;
    }

    @PostMapping("/batches")
    public void takeAndHandleAndSaveDataToDB(@RequestBody byte[] bytes){
        //TODO this method do a lot of work, may be should be better to do adding date and etc in @after of read for this in WWW
        // TODO may be i should save files with names=date of uploading or in different packages
        transformationService.handleRequestBodyData(bytes);
    }

    @GetMapping("/batches")
    public BatchList getListOfAllUploadsOrderedByDate(@RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                                      @RequestParam(value = "limit", defaultValue = "50") Integer limit){

        return new BatchList(transformationService.getBatchesFromDBOrderedByUploadDate(offset, limit));
    }

    @GetMapping("/batches/{batch}/items")
    public ItemList getBatchContent(@RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                    @RequestParam(value = "limit", defaultValue = "50") Integer limit,
                                    @PathVariable Batch batch){
        return new ItemList(transformationService.getItems(offset, limit, batch));
    }
}
