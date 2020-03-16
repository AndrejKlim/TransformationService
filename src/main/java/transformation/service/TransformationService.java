package transformation.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import transformation.domain.Batch;
import transformation.domain.Item;
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
        takeBytesTransformToZipUnpackAndMakeXml(bytes);
        File xml = new File("/home/klim/IdeaProjects/TransformationService/src/main/resources/1.xml");
        saveBatchToDb(xml);
    }

    public void saveBatchToDb(File file){
        Batch batch = new Batch();
        List<Item> items = getItemsFromXml(file);
        batch.setSize(getSizeOfBatch(file));
        batch.setUploadDate(getBatchUploadDate());
        items.forEach(item -> item.setBatch(batch));
        saveItemsToDB(items);
        batch.setItemList(items);
        batchRepository.save(batch);
    }

    public List<Batch> getBatches(int offset, int limit){
        return  batchRepository.findAll(PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "uploadDate")));
    }

    private List<Item> getItemsFromXml(File file){
        // parse xml using DOM model and put data to Item object list with "topic" and "subject" fields filled

        List<Item> itemFromXmlList = new ArrayList<>();

        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(file);

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

    private void takeBytesTransformToZipUnpackAndMakeXml(byte[] bytes){

        //TODO may be i should split this method to 2 or 3 smaller methods

        byte[] buffer = new  byte[2048];

        Path outDir = Paths.get("src/main/resources");

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             BufferedInputStream bis = new BufferedInputStream(byteArrayInputStream);
             ZipInputStream zipInputStream = new ZipInputStream(bis)) // try with resources, close them after
        {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null){
                Path filePAth = outDir.resolve((entry.getName()));

                try(FileOutputStream fos = new FileOutputStream(filePAth.toFile());
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
