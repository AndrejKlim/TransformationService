package transformation.service.archiveCreator;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class ZIPArchiveCreatorService implements IArchiveCreator{

    private static final Logger LOGGER = LogManager.getLogger(ZIPArchiveCreatorService.class);

    @Override
    public File createZIPArchiveFromByteArray(byte[] bytes, String date){

        String zipFileName = SAVE_DIRECTORY.resolve(date).toString() + ".zip";
        File zip = new File(zipFileName);

        try {
            FileUtils.writeByteArrayToFile(zip, bytes);
            LOGGER.info(String.format("Successfully created zip archive, placed in %s", zipFileName));
        } catch (IOException e) {
            LOGGER.error("Error during saving zip archive", e);
        }

        return zip;
    }

}
