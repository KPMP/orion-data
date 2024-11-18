package org.miktmc;

import org.miktmc.packages.Attachment;
import org.miktmc.packages.CustomPackageRepository;
import org.miktmc.logging.LoggingService;
import org.miktmc.packages.Package;
import org.miktmc.packages.PackageRepository;
import org.miktmc.packages.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GenerateChecksums implements CommandLineRunner {

    private CustomPackageRepository customPackageRepository;
    private PackageRepository packageRepository;
    private PackageService packageService;
    private LoggingService logger;

    @Value("${generate-checksums}")
    private Boolean generateChecksums;

    @Autowired
    public GenerateChecksums(CustomPackageRepository customPackageRepository, PackageRepository packageRepository, PackageService packageService, LoggingService logger) {
        this.customPackageRepository = customPackageRepository;
        this.packageRepository = packageRepository;
        this.packageService = packageService;
        this.logger = logger;
    }

    @Override
    public void run(String... args) {
        List<Package> packages = packageRepository.findAll();
        if (generateChecksums) {
            for (Package myPackage : packages) {
                String packageID = myPackage.getPackageId();
                try {
                    List<Attachment> files = packageService.calculateChecksums(packageID);
                    customPackageRepository.updateField(myPackage.getPackageId(), "files", files);
                } catch (Exception e) {
                    logger.logErrorMessage(PackageService.class, null, packageID,
                            PackageService.class.getSimpleName() + ".calculateFileChecksums", "There was a problem calculating the checksum for package " + packageID + ": " + e.getMessage());
                }
            }
        } else {
            logger.logInfoMessage(GenerateChecksums.class, null, null, null, "Not generating checksums. Set generate-checksums to true in application.properties if you want to.");
        }
    }
}
