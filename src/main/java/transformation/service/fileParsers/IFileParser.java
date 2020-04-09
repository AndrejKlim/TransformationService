package transformation.service.fileParsers;

import transformation.domain.entity.Item;

import java.io.File;
import java.util.List;

public interface IFileParser {

    List<Item> getItemsFromFile(File fileToParse, int limit);
}
