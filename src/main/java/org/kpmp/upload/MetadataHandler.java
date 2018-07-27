package org.kpmp.upload;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.kpmp.dao.deprecated.UploadPackageMetadata;
import org.springframework.stereotype.Service;

@Service
public class MetadataHandler {

    public void saveUploadPackageMetadata(UploadPackageMetadata uploadPackageMetadata, String filePath) throws IOException {
        BufferedWriter output = null;
        File file = new File(filePath);
        output = new BufferedWriter(new FileWriter(file));
        output.write(uploadPackageMetadata.generateJSON());
        output.close();
    }

}