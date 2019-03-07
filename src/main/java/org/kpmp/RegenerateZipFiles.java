package org.kpmp;

import java.io.IOException;
import java.util.List;

import org.kpmp.packages.FilePathHelper;
import org.kpmp.packages.Package;
import org.kpmp.packages.PackageRepository;
import org.kpmp.packages.PackageZipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = { "org.kpmp" })
public class RegenerateZipFiles implements CommandLineRunner {

	private PackageRepository packageRepository;
	private PackageZipService zipService;
	private FilePathHelper pathHelper;

	@Autowired
	public RegenerateZipFiles(PackageRepository packageRepository, PackageZipService zipService,
			FilePathHelper pathHelper) {
		this.packageRepository = packageRepository;
		this.zipService = zipService;
		this.pathHelper = pathHelper;
	}

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(RegenerateZipFiles.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<Package> allPackages = packageRepository.findAll();
		for (Package packageInfo : allPackages) {
			String packageId = packageInfo.getPackageId();
			String zipFileName = pathHelper.getZipFileName(packageId);
			if (packageInfo.getRegenerateZip()) {
				try {
					zipService.createZipFile(packageInfo);
					packageInfo.setRegenerateZip(false);
					packageRepository.save(packageInfo);
				} catch (IOException e) {
					System.err.println("Unable to delete file, invalid permissions: " + zipFileName);
				}
			}
		}
	}
}
