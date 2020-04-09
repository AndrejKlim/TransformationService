package transformation.service.fileParsers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
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
@Qualifier("XML")
public class XMLStAXFileParserService implements IFileParser {

    private static final Logger LOGGER = LogManager.getLogger(XMLStAXFileParserService.class);

    int itemsToSkip = 0;

    @Override
    public List<Item> getItemsFromFile(File fileToParse, int limit) {

        List<Item> itemsOut = new ArrayList<>(limit);
        Item item = null;

        try {
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLEventReader reader = xmlInputFactory.createXMLEventReader(new FileInputStream(fileToParse));

            if (reader.hasNext()) {

                skipItems(reader, itemsToSkip);

                while (itemsOut.size() < limit){

                    XMLEvent nextEvent = reader.nextEvent();
                    if (nextEvent.isStartElement()) {
                        StartElement startElement = nextEvent.asStartElement();
                        switch (startElement.getName().getLocalPart()) {
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
                    if (nextEvent.isEndElement()) {
                        EndElement endElement = nextEvent.asEndElement();
                        if (endElement.getName().getLocalPart().equals("item")) {
                            LOGGER.debug(String.format("Item that added to list:\n item body - %s,\n item subject - %s", item.getBody(), item.getSubject()));
                            itemsOut.add(item);
                        }
                    }
                    if(!reader.hasNext()){
                        // if it's end of file, we need to set lines=0 for next files parsing
                        itemsToSkip = 0;
                        return itemsOut;
                    }
                }
            }
        } catch (FileNotFoundException | XMLStreamException e) {
            LOGGER.error("Error during reading xml file", e);
        }
        itemsToSkip += limit;
        return itemsOut;
    }

    private void skipItems(XMLEventReader reader, int linesToSkip){
        // skip needed number of items
        // reader take all events, but item ends after </item>
        // so we iterate all events while found needed and increment counter
        int counterOfSkippedItems = 0;
        while (counterOfSkippedItems < linesToSkip){
            XMLEvent nextEvent;
            try {
                nextEvent = reader.nextEvent();
                if (nextEvent.isEndElement()){
                    EndElement endElement = nextEvent.asEndElement();
                    if (endElement.getName().getLocalPart().equals("item")){
                        counterOfSkippedItems++;
                    }
                }
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }
    }
}
