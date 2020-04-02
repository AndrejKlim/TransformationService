package transformation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import transformation.domain.entity.Batch;
import transformation.domain.entity.Item;
import transformation.repositories.BatchRepository;
import transformation.repositories.ItemRepository;
import transformation.service.archiveCreator.IArchiveCreator;
import transformation.service.archiveUnpacker.IArchiveUnpacker;
import transformation.service.fileParsers.IFileParser;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;




@Service
public class TransformationService {

    private static final Logger LOGGER = LogManager.getLogger(TransformationService.class);

    private final ItemRepository itemRepository;
    private final BatchRepository batchRepository;
    private final IArchiveCreator archiveCreator;
    private final IArchiveUnpacker archiveUnpacker;
    private final IFileParser fileParser;

    public TransformationService(ItemRepository itemRepository,
                                 BatchRepository batchRepository,
                                 IArchiveCreator archiveCreator,
                                 IArchiveUnpacker archiveUnpacker,
                                 @Qualifier("CSV") IFileParser fileParser){
        this.itemRepository = itemRepository;
        this.batchRepository = batchRepository;
        this.archiveCreator = archiveCreator;
        this.archiveUnpacker = archiveUnpacker;
        this.fileParser = fileParser;
    }

    public void handleRequestBodyData(byte[] bytes){
        //method for takeAndHandleAndSaveDataToDB
        String uploadDate = getBatchUploadDate();
        File zipArchive = archiveCreator.createZIPArchiveFromByteArray(bytes, uploadDate);
        File directoryWithXmlFiles = archiveUnpacker.unpackArchive(zipArchive, uploadDate);
        createAndSaveBatchToDb(directoryWithXmlFiles, uploadDate);
    }



    public String getBatchesFromDBInJsonOrderedByUploadDate(int offset, int limit){
        ObjectMapper objectMapper = new ObjectMapper();
        String response = "";
        try {
            response = objectMapper.writeValueAsString(getBatchesFromDBOrderedByUploadDate(offset, limit));
            LOGGER.debug(String.format("Batch list response %s", response));
        } catch (JsonProcessingException e) {
            LOGGER.error("Error during Batch list Json response creating", e);
        }
        return response;
    }

    public String getItemsFromDBByBatchInJson(int offset, int limit, Batch batch){
        //method for getBatchContent method in controller
        ObjectMapper objectMapper = new ObjectMapper();
        String response = "";
        try {
            response = objectMapper.writeValueAsString(itemRepository.findAllByBatch_Id(batch.getId(), PageRequest.of(offset, limit)));
            LOGGER.debug(String.format("Item list response - %s", response));
        } catch (JsonProcessingException e) {
            LOGGER.error("Error during Item list Json response creating", e);
        }
        return response;
    }

    private List<Batch> getBatchesFromDBOrderedByUploadDate(int offset, int limit){
        // method for getListOfAllUploadsOrderedByDate method in controller
        return  batchRepository.findAll(PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "uploadDate")));
    }

    private void createAndSaveBatchToDb(File dirWithXmlFiles, String date){
        Batch batch = new Batch();
        List<Item> items = new ArrayList<>();
        int size = 0;
        if (dirWithXmlFiles.listFiles() != null){
            for (File file : dirWithXmlFiles.listFiles()){
                List<Item> tempList = fileParser.getItemsFromFile(file);
                items.addAll(tempList);

                size = size + getSizeOfBatch(file);
            }
        }
        batch.setSize(size);
        batch.setUploadDate(date);
        items.forEach(item -> item.setBatch(batch));
        batch.setItemList(items);
        LOGGER.debug(String.format("Current batch status:\n batch size - %d;\n batch date - %s;\n item list size - %d", size, date, items.size()));
        batchRepository.save(batch);
    }

    private String getBatchUploadDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private Integer getSizeOfBatch(File file){
        return (int) file.length();
    }
}
