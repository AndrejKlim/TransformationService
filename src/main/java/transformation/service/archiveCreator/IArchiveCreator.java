package transformation.service.archiveCreator;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface IArchiveCreator {

    static final Path SAVE_DIRECTORY = Paths.get("src/main/resources");

    File createZIPArchiveFromByteArray(byte[] bytes, String date);


}
