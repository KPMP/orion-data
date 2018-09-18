package org.kpmp.packages;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FilePathHelper {

    @Value("${file.base.path}")
    private String basePath;

    @Value("${metadata.file.name}")
    private String metadataFileName;

    public String getPackagePath(String prefix, String suffix) {
        return basePath + File.separator + prefix + "package_" + suffix + File.separator;
    }

    public String getMetadataFileName() {
        return metadataFileName;
    }


}