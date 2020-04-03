package transformation.service.factory;

import transformation.service.fileParsers.IFileParser;

public interface IFileParserFactory {

    IFileParser getParser(String fileFormat);
}
