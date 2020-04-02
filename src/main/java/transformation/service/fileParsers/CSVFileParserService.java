package transformation.service.fileParsers;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import transformation.domain.entity.Item;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Qualifier("CSV")
public class CSVFileParserService implements IFileParser {

    private static final Logger LOGGER = LogManager.getLogger(CSVFileParserService.class);

    @Override
    public List<Item> getItemsFromFile(File fileToParse) {

        List<Item> itemsOut = new ArrayList<>();

        try {
            CSVReader csvReader = new CSVReader(new FileReader(fileToParse));
            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null){
                Item item = new Item();
                item.setSubject(nextLine[1]);
                item.setBody(nextLine[2]);
                LOGGER.debug(String.format("Item that added to list:\n item body - %s,\n item subject - %s", nextLine[1],nextLine[2]));
                itemsOut.add(item);
            }
        } catch (IOException | CsvValidationException e) {
            LOGGER.error("Error during reading csv file", e);
        }
        return itemsOut;
    }
}
