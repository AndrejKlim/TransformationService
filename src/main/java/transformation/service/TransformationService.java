package transformation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import transformation.domain.entity.Batch;
import transformation.domain.entity.Item;
import transformation.repositories.BatchRepository;
import transformation.repositories.ItemRepository;
import transformation.service.archiveCreator.IArchiveCreator;
import transformation.service.archiveUnpacker.IArchiveUnpacker;
import transformation.service.fileParsers.IFileParser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;




@Service
public class TransformationService {

    private final ItemRepository itemRepository;
    private final BatchRepository batchRepository;
    private final IArchiveCreator archiveCreator;
    private final IArchiveUnpacker archiveUnpacker;
    private final IFileParser fileParser;

    public TransformationService(ItemRepository itemRepository,
                                 BatchRepository batchRepository,
                                 IArchiveCreator archiveCreator,
                                 IArchiveUnpacker archiveUnpacker,
                                 IFileParser fileParser){
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
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String getItemsFromDBByBatchInJson(int offset, int limit, Batch batch){
        //method for getBatchContent method in controller
        ObjectMapper objectMapper = new ObjectMapper();
        String response = "";
        try {
            response = objectMapper.writeValueAsString(itemRepository.findAllByBatch_Id(batch.getId(), PageRequest.of(offset, limit)));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
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
            for (File xml : dirWithXmlFiles.listFiles()){
                List<Item> tempList = fileParser.getItemsFromFile(xml);
                items.addAll(tempList);

                size = size + getSizeOfBatch(xml);
            }
        }
        batch.setItemList(items);
        batch.setSize(size);
        batch.setUploadDate(date);
        items.forEach(item -> item.setBatch(batch));
        batch.setItemList(items);
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
