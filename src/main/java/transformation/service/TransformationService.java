package transformation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import transformation.domain.entity.Batch;
import transformation.domain.entity.Item;
import transformation.repositories.BatchRepository;
import transformation.repositories.ItemRepository;
import transformation.service.archiveCreator.IArchiveCreator;
import transformation.service.archiveUnpacker.IArchiveUnpacker;
import transformation.service.factory.IFileParserFactory;
import transformation.service.fileParsers.IFileParser;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Service
public class TransformationService {

    private static final Logger LOGGER = LogManager.getLogger(TransformationService.class);

    private final ItemRepository itemRepository;
    private final BatchRepository batchRepository;
    private final IArchiveCreator archiveCreator;
    private final IArchiveUnpacker archiveUnpacker;
    private final IFileParserFactory fileParserFactory;

    private IFileParser fileParser;

    public TransformationService(ItemRepository itemRepository,
                                 BatchRepository batchRepository,
                                 IArchiveCreator archiveCreator,
                                 IArchiveUnpacker archiveUnpacker,
                                 IFileParserFactory iFileParserFactory) {
        this.itemRepository = itemRepository;
        this.batchRepository = batchRepository;
        this.archiveCreator = archiveCreator;
        this.archiveUnpacker = archiveUnpacker;
        this.fileParserFactory = iFileParserFactory;
    }

    public void handleRequestBodyData(byte[] bytes){
        //method for takeAndHandleAndSaveDataToDB
        String uploadDate = getBatchUploadDate();
        File zipArchive = archiveCreator.createZIPArchiveFromByteArray(bytes, uploadDate);
        File directoryWithFilesToParse = archiveUnpacker.unpackArchive(zipArchive, uploadDate);

        //let all files in archive have same extensions
        String filesExt = getFileExtension(getAllFilesInDirectory(directoryWithFilesToParse).get(0));
        fileParser = fileParserFactory.getParser(filesExt);
        // TODO read how to take limit from config or properties files, not hardcoded
        createAndSaveBatchToDb(directoryWithFilesToParse, uploadDate, 100);
    }



    public String getBatchesFromDBInJsonOrderedByUploadDate(int offset, int limit){
        ObjectMapper objectMapper = new ObjectMapper();
        String response = "";
        try {
            response = objectMapper.writeValueAsString(getBatchesFromDBOrderedByUploadDate(offset, limit));
            LOGGER.info(String.format("Batch list response %s", response));
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
            LOGGER.info(String.format("Item list response - %s", response));
        } catch (JsonProcessingException e) {
            LOGGER.error("Error during Item list Json response creating", e);
        }
        return response;
    }

    private List<Batch> getBatchesFromDBOrderedByUploadDate(int offset, int limit){
        // method for getListOfAllUploadsOrderedByDate method in controller
        return  batchRepository.findAll(PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "uploadDate")));
    }

    private void createAndSaveBatchToDb(File dirWithXmlFiles, String date, int limit){

        Batch batch = new Batch();
        List<Item> items;
        int size = 0;
        batchRepository.save(batch);

        for (File file : getAllFilesInDirectory(dirWithXmlFiles)){

            do{
                items = fileParser.getItemsFromFile(file, limit);
                items.forEach(item -> item.setBatch(batch));
                itemRepository.saveAll(items);
            }while (items.size() >= limit);
            size = size + getSizeOfBatch(file);

            LOGGER.info(String.format("Parsing file %s ended", file.getPath()));
        }

        batch.setSize(size);
        batch.setUploadDate(date);
        batchRepository.save(batch);
    }

    private String getFileExtension(File file){
        return file.getName().substring(file.getName().length()-3);
    }

    private List<File> getAllFilesInDirectory(File directory){
        List<File> files = new ArrayList<>();
        if (directory.isDirectory() && (directory.listFiles() != null)){
            files.addAll(Arrays.asList(directory.listFiles()));
        }
        return files;
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
