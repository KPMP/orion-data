package org.kpmp;

import java.util.List;

import org.kpmp.dao.UploadPackage;
import org.kpmp.upload.MetadataHandler;
import org.kpmp.upload.UploadPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableCaching
@ComponentScan(basePackages = { "org.kpmp" })
public class GenerateMetadataFiles implements CommandLineRunner {

    private UploadPackageRepository uploadPackageRepository;
    private MetadataHandler metadataHandler;

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
        for (UploadPackage uploadPackage : packages) {
            metadataHandler.saveUploadPackageMetadata(uploadPackage);
        }
    }


}
