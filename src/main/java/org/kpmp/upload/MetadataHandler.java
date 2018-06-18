package org.kpmp.upload;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.kpmp.dao.FileSubmission;
import org.kpmp.dao.UploadPackage;
import org.kpmp.dao.UploadPackageMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MetadataHandler {

    @Value("${metadata.file.name}")
    private String metadataFileName;

    public void saveUploadPackageMetadata(UploadPackageMetadata uploadPackageMetadata, String filePath) throws IOException {
        BufferedWriter output = null;
        File file = new File(filePath);
        output = new BufferedWriter(new FileWriter(file));
        output.write(uploadPackageMetadata.generateJSON());
        output.close();
    }

    public String getFilePathFromUploadPackage(UploadPackage uploadPackage) {
        FileSubmission firstFile = uploadPackage.getFileSubmissions().get(0);
        String[] filePathArr = firstFile.getFilePath().split("\\\\");
        filePathArr[filePathArr.length - 1] = "";
        String filePath = String.join("\\", filePathArr);
        return filePath + metadataFileName;
    }
}