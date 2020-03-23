package transformation.service.fileParsers;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import transformation.domain.entity.Item;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Qualifier("CSV")
public class CSVFileParserService implements IFileParser {

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
                itemsOut.add(item);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }
        return itemsOut;
    }
}
