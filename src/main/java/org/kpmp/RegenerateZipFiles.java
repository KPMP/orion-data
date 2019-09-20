package org.kpmp;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.kpmp.externalProcess.CommandBuilder;
import org.kpmp.externalProcess.ProcessExecutor;
import org.kpmp.packages.CustomPackageRepository;
import org.kpmp.packages.FilePathHelper;
import org.kpmp.packages.PackageKeys;
import org.kpmp.zip.ZipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = { "org.kpmp" })
public class RegenerateZipFiles implements CommandLineRunner {

	private CustomPackageRepository packageRepository;
	private FilePathHelper pathHelper;
	private CommandBuilder commandBuilder;
	private ProcessExecutor processExecutor;
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private ZipService zipService;

	@Autowired
	public RegenerateZipFiles(CustomPackageRepository packageRepository, CommandBuilder commandBuilder,
			FilePathHelper pathHelper, ProcessExecutor processExecutor, ZipService zipService) {
		this.packageRepository = packageRepository;
		this.commandBuilder = commandBuilder;
		this.pathHelper = pathHelper;
		this.processExecutor = processExecutor;
		this.zipService = zipService;
	}

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(RegenerateZipFiles.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<JSONObject> jsons = packageRepository.findAll();
		for (JSONObject packageInfo : jsons) {
			String packageId = packageInfo.getString(PackageKeys.ID.getKey());
			String packageMetadata = packageRepository.getJSONByPackageId(packageId);
			String zipFileName = pathHelper.getZipFileName(packageId);
			if (packageInfo.getBoolean(PackageKeys.REGENERATE_ZIP.getKey())) {
				Date startRezipTime = new Date();
				log.info("Rezipping package: " + packageId);
				try {
					File existingZipFile = new File(zipFileName);
					existingZipFile.delete();
//					String[] zipCommand = commandBuilder.buildZipCommand(packageId, packageMetadata);
//					boolean success = processExecutor.executeProcess(zipCommand);
					String packagePath = pathHelper.getPackagePath(packageId);
					List<String> fileNames = pathHelper.getFilenames(packagePath);
					List<String> filePaths = new ArrayList<>();
					for (String fileName : fileNames) {
						filePaths.add(packagePath + File.separator + fileName);
					}

					Map<String, String> additionalData = new HashMap<String, String>();
					additionalData.put("metadata.json", packageMetadata);
					zipService.createZipFile(zipFileName, filePaths, additionalData);
					LocalDateTime start = LocalDateTime.ofInstant(startRezipTime.toInstant(), ZoneId.systemDefault());
					LocalDateTime end = LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
					long totalTime = ChronoUnit.SECONDS.between(start, end);
					log.info("Timing: " + totalTime + " seconds");
					packageRepository.updateField(packageId, PackageKeys.REGENERATE_ZIP.getKey(), false);
				} catch (IOException e) {
					log.error("Unable to delete file, invalid permissions: " + zipFileName);
				}
			}
		}
	}

}
