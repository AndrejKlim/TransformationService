package transformation.controllers;

import org.springframework.web.bind.annotation.*;
import transformation.domain.entity.Batch;
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
        //TODO may be i should save files with names=date of uploading or in different packages
        transformationService.handleRequestBodyData(bytes);
    }
    //TODO in task we take set of xml files in zip archive, so fix it

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
