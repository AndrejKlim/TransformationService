package transformation.service.fileParsers;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import transformation.domain.entity.Item;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class XMLDOMFileParserService implements IFileParser {

    @Override
    public List<Item> getItemsFromFile(File fileToParse) {

        List<Item> itemFromXmlList = new ArrayList<>();

        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(fileToParse);

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
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return itemFromXmlList;
    }
}
