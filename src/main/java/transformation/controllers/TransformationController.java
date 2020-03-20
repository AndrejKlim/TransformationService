package transformation.controllers;

import org.springframework.web.bind.annotation.*;
import transformation.domain.entity.Batch;
import transformation.service.TransformationService;
import transformation.service.archiveCreator.ZIPArchiveCreatorService;
import transformation.service.archiveUnpacker.ZIPArchiveUnpackerService;


@RestController
public class TransformationController {

    private final TransformationService transformationService;
    private final ZIPArchiveCreatorService zipArchiveCreatorService;
    private final ZIPArchiveUnpackerService zipArchiveUnpackerService;

    public TransformationController(TransformationService transformationService, ZIPArchiveCreatorService zipArchiveCreatorService, ZIPArchiveUnpackerService zipArchiveUnpackerService) {
        this.transformationService = transformationService;
        this.zipArchiveCreatorService = zipArchiveCreatorService;
        this.zipArchiveUnpackerService = zipArchiveUnpackerService;
    }

    @PostMapping("/batches")
    public void takeAndHandleAndSaveDataToDB(@RequestBody byte[] bytes){
        //TODO this method do a lot of work, may be should be better to do adding date and etc in @after of read for this in WWW
        //The URI of the created member resource is automatically assigned and returned in the response Location header field.  - return
        transformationService.handleRequestBodyData(bytes);
        //return null;
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
