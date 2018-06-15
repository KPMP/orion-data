package org.kpmp;

import java.text.MessageFormat;
import java.util.List;

import org.kpmp.dao.UploadPackage;
import org.kpmp.upload.MetadataHandler;
import org.kpmp.upload.UploadPackageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@EnableCaching
@ComponentScan(basePackages = { "org.kpmp" })
public class GenerateMetadataFiles implements CommandLineRunner {

    private UploadPackageRepository uploadPackageRepository;
    private MetadataHandler metadataHandler;

    private static final MessageFormat logMessage = new MessageFormat("metadata for package {0} created");
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public GenerateMetadataFiles(UploadPackageRepository uploadPackageRepository, MetadataHandler metadataHandler) {
        this.uploadPackageRepository = uploadPackageRepository;
        this.metadataHandler = metadataHandler;
    }

    public static void main(String[] args) {
        SpringApplication.run(GenerateMetadataFiles.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        List<UploadPackage> packages = uploadPackageRepository.findAll();
        int packageCount = 0;
        for (UploadPackage uploadPackage : packages) {
            metadataHandler.saveUploadPackageMetadata(uploadPackage);
            log.info(logMessage.format(new Object[]{uploadPackage.getId()}));
            packageCount++;
        }
        log.info(packageCount + " metadata files created");
    }


}
