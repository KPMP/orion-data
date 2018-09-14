package org.kpmp;

import java.text.MessageFormat;
import java.util.List;

import org.kpmp.dao.deprecated.UploadPackage;
import org.kpmp.dao.deprecated.UploadPackageMetadata;
import org.kpmp.packages.FilePathHelper;
import org.kpmp.upload.deprecated.MetadataHandler;
import org.kpmp.upload.deprecated.UploadPackageMySQLRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@EnableCaching
@ComponentScan(basePackages = { "org.kpmp" })
public class GenerateMetadataFiles implements CommandLineRunner {

	private UploadPackageMySQLRepository uploadPackageRepository;
	private MetadataHandler metadataHandler;
	private FilePathHelper filePathHelper;

	private static final MessageFormat logMessage = new MessageFormat("metadata for package {0} created");
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	public GenerateMetadataFiles(UploadPackageMySQLRepository uploadPackageRepository, MetadataHandler metadataHandler,
			FilePathHelper filePathHelper) {
		this.uploadPackageRepository = uploadPackageRepository;
		this.metadataHandler = metadataHandler;
		this.filePathHelper = filePathHelper;
	}

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(GenerateMetadataFiles.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<UploadPackage> packages = uploadPackageRepository.findAll();
		int packageCount = 0;
		for (UploadPackage uploadPackage : packages) {
			String filePath = filePathHelper.getPackagePath("", Integer.toString(uploadPackage.getId()))
					+ filePathHelper.getMetadataFileName();
			UploadPackageMetadata uploadPackageMetadata = new UploadPackageMetadata(uploadPackage);
			metadataHandler.saveUploadPackageMetadata(uploadPackageMetadata, filePath);
			log.info(logMessage.format(new Object[] { uploadPackage.getId() }));
			packageCount++;
		}
		log.info(packageCount + " metadata files created");
	}

}
