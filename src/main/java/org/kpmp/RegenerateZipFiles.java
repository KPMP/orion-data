package org.kpmp;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.kpmp.cache.CacheController;
import org.kpmp.externalProcess.CommandBuilder;
import org.kpmp.externalProcess.ProcessExecutor;
import org.kpmp.filters.AuthorizationFilter;
import org.kpmp.globus.GlobusAuthorizationCodeInstalledApp;
import org.kpmp.globus.GlobusService;
import org.kpmp.packages.CustomPackageRepository;
import org.kpmp.packages.FilePathHelper;
import org.kpmp.packages.PackageController;
import org.kpmp.packages.PackageFileHandler;
import org.kpmp.packages.PackageKeys;
import org.kpmp.packages.PackageRepository;
import org.kpmp.packages.PackageService;
import org.kpmp.packages.StateHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@ComponentScan(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
		PackageRepository.class, FilePathHelper.class, CommandBuilder.class, ProcessExecutor.class,
		CustomPackageRepository.class,
		PackageFileHandler.class }), excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
				PackageController.class, WebConfig.class, Application.class, AuthorizationFilter.class,
				GlobusAuthorizationCodeInstalledApp.class, GlobusService.class, StateHandlerService.class,
				PackageService.class, CacheController.class }))
public class RegenerateZipFiles implements CommandLineRunner {

	private CustomPackageRepository packageRepository;
	private FilePathHelper pathHelper;
	private CommandBuilder commandBuilder;
	private ProcessExecutor processExecutor;
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private PackageFileHandler packageFileHandler;

	@Autowired
	public RegenerateZipFiles(CustomPackageRepository packageRepository, CommandBuilder commandBuilder,
			FilePathHelper pathHelper, ProcessExecutor processExecutor, PackageFileHandler packageFileHandler) {
		this.packageRepository = packageRepository;
		this.commandBuilder = commandBuilder;
		this.pathHelper = pathHelper;
		this.processExecutor = processExecutor;
		this.packageFileHandler = packageFileHandler;
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
			File metadataFile = packageFileHandler.saveFile(packageMetadata, packageId, "metadata.json", true);
			String zipFileName = pathHelper.getZipFileName(packageId);
			if (packageInfo.getBoolean(PackageKeys.REGENERATE_ZIP.getKey())) {
				Date startRezipTime = new Date();
				log.info("Rezipping package: " + packageId);
				try {
					File existingZipFile = new File(zipFileName);
					existingZipFile.delete();
					String[] zipCommand = commandBuilder.buildZipCommand(packageId,
							"/home/pathadmin/apps/zipWorker/build/libs/zipWorker.jar");

					boolean success = processExecutor.executeProcess(zipCommand);
					metadataFile.delete();
					if (success) {
						LocalDateTime start = LocalDateTime.ofInstant(startRezipTime.toInstant(),
								ZoneId.systemDefault());
						LocalDateTime end = LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
						long totalTime = ChronoUnit.SECONDS.between(start, end);
						log.info("Timing: " + totalTime + " seconds");
						packageRepository.updateField(packageId, PackageKeys.REGENERATE_ZIP.getKey(), false);
					} else {
						log.error("Unable to zip files for package " + packageId);
					}
				} catch (IOException e) {
					log.error("Unable to delete file, invalid permissions: " + zipFileName);
				}
			}
		}
	}

}
