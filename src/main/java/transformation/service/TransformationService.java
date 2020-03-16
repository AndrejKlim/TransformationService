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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;




@Service
public class TransformationService {

    private final ItemRepository itemRepository;
    private final BatchRepository batchRepository;

    public TransformationService(ItemRepository itemRepository, BatchRepository batchRepository){
        this.itemRepository = itemRepository;
        this.batchRepository = batchRepository;
    }

    public void handleRequestBodyData(byte[] bytes){
        //method for takeAndHandleAndSaveDataToDB
        //TODO takeBytesTransformToZipUnpackAndMakeXml can return file name, next this file should be saved to db
        File directoryWithXmlFiles = takeBytesTransformToZipUnpackAndMakeXml(bytes);
        saveBatchToDb(directoryWithXmlFiles);
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

    private void saveBatchToDb(File dirWithXmlFiles){
        Batch batch = new Batch();
        List<Item> items = new ArrayList<>();
        int size = 0;
        if (dirWithXmlFiles.listFiles() != null){
            for (File xml : dirWithXmlFiles.listFiles()){
                List<Item> tempList = getItemsFromXml(xml);
                items.addAll(tempList);

                size = size + getSizeOfBatch(xml);
            }
        }
        batch.setItemList(items);
        batch.setSize(size);
        batch.setUploadDate(getBatchUploadDate());
        items.forEach(item -> item.setBatch(batch));
        batch.setItemList(items);
        batchRepository.save(batch);
    }


    private List<Item> getItemsFromXml(File xmlFile){
        // parse xml using DOM model and put data to Item object list with "topic" and "subject" fields filled
        // file - File object that represents xml file
        // return - List of item
        List<Item> itemFromXmlList = new ArrayList<>();

        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(xmlFile);

            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("item");

            for (int temp = 0; temp < nodeList.getLength(); temp++) {
                Node node = nodeList.item(temp);
                Item item = new Item();
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    item.setSubject(element.getElementsByTagName("topic").item(0).getTextContent());
                    item.setBody(element.getElementsByTagName("content").item(0).getTextContent());
                }
                itemFromXmlList.add(item);
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return itemFromXmlList;
    }

    private File takeBytesTransformToZipUnpackAndMakeXml(byte[] bytes){
        // bytes - byte array received from request body, which are zip archive
        // return - File object that represents directory where the unpacked files are located
        //TODO may be i should split this method to 2 or 3 smaller methods

        byte[] buffer = new  byte[2048];

        Path outDir = Paths.get("src/main/resources");
        //TODO may be i should connect upload date what writes to directory and what writes to DB (mismatch is near 0,5 s) for big files it can be greater
        File zipEntriesDirectory = new File(outDir.resolve(getBatchUploadDate()).toString()); // create dir with name == date

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             BufferedInputStream bis = new BufferedInputStream(byteArrayInputStream);
             ZipInputStream zipInputStream = new ZipInputStream(bis)) // try with resources, close them after
        {
            ZipEntry entry;

            zipEntriesDirectory.mkdir();
            while ((entry = zipInputStream.getNextEntry()) != null){
                Path filePath = Paths.get(zipEntriesDirectory.toURI());

                filePath = filePath.resolve((entry.getName()));

                try(FileOutputStream fos = new FileOutputStream(filePath.toFile());
                    BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length)){

                    int len;
                    while ((len = zipInputStream.read(buffer)) > 0){
                        bos.write(buffer,0,len);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return zipEntriesDirectory;
    }

    private void saveItemsToDB(List<Item> items){
        for (Item item : items) {
            itemRepository.save(item);
        }
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
