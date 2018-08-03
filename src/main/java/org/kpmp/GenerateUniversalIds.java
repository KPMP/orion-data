package org.kpmp;

import java.util.List;

import org.kpmp.dao.FileSubmission;
import org.kpmp.dao.UploadPackage;
import org.kpmp.upload.UploadPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "org.kpmp" })
public class GenerateUniversalIds implements CommandLineRunner {

	private UploadPackageRepository packageRepository;
	private UniversalIdGenerator uuidGenerator;

	@Autowired
	public GenerateUniversalIds(UploadPackageRepository packageRepository, UniversalIdGenerator uuidGenerator) {
		this.packageRepository = packageRepository;
		this.uuidGenerator = uuidGenerator;
	}

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(GenerateUniversalIds.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<UploadPackage> uploadPackages = packageRepository.findAll();
		for (UploadPackage uploadPackage : uploadPackages) {
			String uuid = uuidGenerator.generateUniversalId();
			uploadPackage.setUniversalId(uuid);
			List<FileSubmission> fileSubmissions = uploadPackage.getFileSubmissions();
			for (FileSubmission fileSubmission : fileSubmissions) {
				String fileUuid = uuidGenerator.generateUniversalId();
				fileSubmission.setUniversalId(fileUuid);
			}
			packageRepository.save(uploadPackage);
		}
	}

}
