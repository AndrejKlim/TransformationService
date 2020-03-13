package transformation.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import transformation.service.TransformationService;


@RestController
public class TransformationController {

    private final TransformationService transformationService;

    public TransformationController(TransformationService transformationService) {
        this.transformationService = transformationService;
    }

    @PostMapping("/batches")
    public void takeAndHandleAndSaveDataToDB(@RequestBody byte[] bytes){

        transformationService.takeBytesTransformToZipUnpackAndMakeXml(bytes);

    }


    @GetMapping("/batches")
    public String getListOfAllUploadsOrderedByDate(){
        return null;
    }

    @GetMapping("/batches/{batchId}")
    public String getBatchContent(){
        return null;
    }
}
