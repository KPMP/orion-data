package org.kpmp;

import com.google.api.client.json.Json;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kpmp.packages.Attachment;
import org.kpmp.packages.CustomPackageRepository;
import org.kpmp.packages.Package;
import org.kpmp.packages.PackageKeys;
import org.kpmp.packages.PackageRepository;
import org.kpmp.packages.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GenerateChecksums implements CommandLineRunner {

    CustomPackageRepository customPackageRepository;
    private PackageRepository packageRepository;
    private PackageService packageService;

    @Value("${generate-checksums}")
    private Boolean generateChecksums;

    @Autowired
    public GenerateChecksums(CustomPackageRepository customPackageRepository, PackageRepository packageRepository, PackageService packageService) {
        this.customPackageRepository = customPackageRepository;
        this.packageRepository = packageRepository;
        this.packageService = packageService;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Package> packages = packageRepository.findAll();
        for (Package myPackage: packages) {
            List<Attachment> files = packageService.calculateChecksums(myPackage);
            customPackageRepository.updateField(myPackage.getPackageId(), "files", files);
        }
    }
}
