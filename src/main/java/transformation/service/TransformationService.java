package transformation.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class TransformationService {

    public TransformationService(){

    }

    public void takeBytesTransformToZipUnpackAndMakeXml(byte[] bytes){

        byte[] buffer = new  byte[2048];

        Path outDir = Paths.get("src/main/resources");

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             BufferedInputStream bis = new BufferedInputStream(byteArrayInputStream);
             ZipInputStream zipInputStream = new ZipInputStream(bis)) // try with resources, closes them after
        {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null){
                Path filePAth = outDir.resolve((entry.getName()));

                try(FileOutputStream fos = new FileOutputStream(filePAth.toFile());
                    BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length)){

                    int len;
                    while ((len = zipInputStream.read(buffer)) > 0){
                        bos.write(buffer,0,len);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parceXmlToItem(){

    }
}
