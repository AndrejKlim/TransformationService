package transformation.service.archiveCreator;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class ZIPArchiveCreatorService implements IArchiveCreator{

    @Override
    public File createZIPArchiveFromByteArray(byte[] bytes, String date){

        String zipFileName = SAVE_DIRECTORY.resolve(date).toString() + ".zip";
        File zip = new File(zipFileName);

        try {
            FileUtils.writeByteArrayToFile(zip, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return zip;
    }

}
