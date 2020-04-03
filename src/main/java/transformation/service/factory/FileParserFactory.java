package transformation.service.factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import transformation.service.fileParsers.CSVFileParserService;
import transformation.service.fileParsers.IFileParser;
import transformation.service.fileParsers.XMLStAXFileParserService;

@Component
public class FileParserFactory implements IFileParserFactory {

    private static final Logger LOGGER = LogManager.getLogger(FileParserFactory.class);

    private final CSVFileParserService csvFileParserService;
    private final XMLStAXFileParserService xmlStAXFileParserService;

    IFileParser iFileParser;

    public FileParserFactory(CSVFileParserService csvFileParserService, XMLStAXFileParserService xmlStAXFileParserService) {
        this.csvFileParserService = csvFileParserService;
        this.xmlStAXFileParserService = xmlStAXFileParserService;
    }

    @Override
    public IFileParser getParser(String fileFormat) {

        if (fileFormat.equals("csv")){
            iFileParser = csvFileParserService;
            LOGGER.debug("Created CSV file reader");
        }else if (fileFormat.equals("xml")){
            iFileParser = xmlStAXFileParserService;
            LOGGER.debug("Created XML file reader");
        }
        return iFileParser;
    }
}
