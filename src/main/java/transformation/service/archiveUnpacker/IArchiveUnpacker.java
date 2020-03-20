package transformation.service.archiveUnpacker;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface IArchiveUnpacker {

    static final Path SAVE_DIRECTORY = Paths.get("src/main/resources");

    File unpackArchive(File archive, String date);
}
