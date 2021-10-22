package org.kpmp;

import org.kpmp.packages.Package;
import org.kpmp.packages.PackageRepository;
import org.kpmp.packages.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GenerateChecksums implements CommandLineRunner {
    private PackageRepository packageRepository;
    private PackageService packageService;

    @Value("${generate-checksums}")
    private Boolean generateChecksums;

    @Autowired
    public GenerateChecksums(PackageRepository packageRepository, PackageService packageService) {
        this.packageRepository = packageRepository;
        this.packageService = packageService;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Package> packages = packageRepository.findAll();
        if (generateChecksums) {
            for (Package myPackage : packages) {
                System.out.println("Generating checksums for " + myPackage.getPackageId());
                packageService.calculateAndSaveChecksums(myPackage.getPackageId());
            }
        } else {
            System.out.println("Not generating checksums");
        }
    }
}
