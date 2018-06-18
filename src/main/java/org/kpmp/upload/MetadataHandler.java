package org.kpmp.upload;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.kpmp.dao.UploadPackageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MetadataHandler {



    FilePathHelper filePathHelper;

    @Autowired
    public MetadataHandler(FilePathHelper filePathHelper) {
        this.filePathHelper = filePathHelper;
    }

    public void saveUploadPackageMetadata(UploadPackageMetadata uploadPackageMetadata, String filePath) throws IOException {
        BufferedWriter output = null;
        File file = new File(filePath);
        output = new BufferedWriter(new FileWriter(file));
        output.write(uploadPackageMetadata.generateJSON());
        output.close();
    }

}