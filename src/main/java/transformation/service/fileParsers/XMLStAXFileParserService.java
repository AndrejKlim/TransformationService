package transformation.service.fileParsers;

import org.springframework.stereotype.Service;
import transformation.domain.entity.Item;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
public class XMLStAXFileParserService implements IFileParser {

    @Override
    public List<Item> getItemsFromFile(File fileToParse) {

        List<Item> items = new ArrayList<>();
        Item item = null;
        try {
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(new FileInputStream(fileToParse));

            while (reader.hasNext()){

                XMLEvent nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()){
                    StartElement startElement = nextEvent.asStartElement();
                    switch (startElement.getName().getLocalPart()){
                        case "item":
                            item = new Item();
                            break;
                        case "topic":
                            nextEvent = reader.nextEvent();
                            item.setSubject(nextEvent.asCharacters().getData());
                            break;
                        case "content":
                            nextEvent = reader.nextEvent();
                            item.setBody(nextEvent.asCharacters().getData());
                            break;
                    }
                }
                if (nextEvent.isEndElement()){
                    EndElement endElement = nextEvent.asEndElement();
                    if (endElement.getName().getLocalPart().equals("item")){
                        items.add(item);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

        return items;
    }
}
