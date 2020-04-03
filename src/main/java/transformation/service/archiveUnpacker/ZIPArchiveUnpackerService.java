package transformation.service.archiveUnpacker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ZIPArchiveUnpackerService implements IArchiveUnpacker {

    private static final Logger LOGGER = LogManager.getLogger(ZIPArchiveUnpackerService.class);

    @Override
    public File unpackArchive(File archive, String date) {

        byte[] buffer = new  byte[2048];
        File dirToSaveUnpackedFiles = SAVE_DIRECTORY.resolve(date).toFile();
        LOGGER.debug(String.format("Taken zip archive, %s", dirToSaveUnpackedFiles.getName()));
        dirToSaveUnpackedFiles.mkdir();
        try (FileInputStream fis = new FileInputStream(archive);
             ZipInputStream zis = new ZipInputStream(fis)){

            ZipEntry zipEntry;

            while ((zipEntry = zis.getNextEntry()) != null){

                Path filePath = Paths.get(dirToSaveUnpackedFiles.toURI()).resolve(zipEntry.getName());

                try (FileOutputStream fos = new FileOutputStream(filePath.toFile());
                     BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length)){

                    int len;
                    while ((len = zis.read(buffer)) > 0){
                        bos.write(buffer, 0, len);
                    }
                }
                LOGGER.info(String.format("Created unpacked file %s", filePath.toString()));
            }
        } catch (IOException e) {
            LOGGER.error("Error during unpacking zip archive", e);
        }
        return dirToSaveUnpackedFiles;
    }
}
