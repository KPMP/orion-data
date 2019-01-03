package org.kpmp.packages;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kpmp.UniversalIdGenerator;
import org.kpmp.users.User;
import org.kpmp.users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PackageService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private static final MessageFormat zipPackage = new MessageFormat("Service|{0}|{1}");

	private PackageRepository packageRepository;
	private UserRepository userRepository;
	private UniversalIdGenerator universalIdGenerator;
	private PackageFileHandler packageFileHandler;
	private PackageZipService packageZipper;
	private FilePathHelper filePathHelper;

	@Autowired
	public PackageService(PackageRepository packageRepository, UserRepository userRepository,
			UniversalIdGenerator universalIdGenerator, PackageFileHandler packageFileHandler,
			PackageZipService packageZipper, FilePathHelper filePathHelper) {
		this.packageRepository = packageRepository;
		this.userRepository = userRepository;
		this.filePathHelper = filePathHelper;
		this.universalIdGenerator = universalIdGenerator;
		this.packageFileHandler = packageFileHandler;
		this.packageZipper = packageZipper;

	}

	public List<PackageView> findAllPackages() {
		List<Package> packages = packageRepository.findAll(new Sort(Sort.Direction.DESC, "createdAt"));
		List<PackageView> packageViews = new ArrayList<>();
		for (Package packageToCheck : packages) {
			PackageView packageView = new PackageView(packageToCheck);
			String zipFileName = filePathHelper.getZipFileName(packageToCheck.getPackageId());
			if (new File(zipFileName).exists()) {
				packageView.setIsDownloadable(true);
			} else {
				packageView.setIsDownloadable(false);
			}
			packageViews.add(packageView);
		}
		return packageViews;
	}

	public Path getPackageFile(String packageId) {
		String zipFileName = filePathHelper.getZipFileName(packageId);
		Path filePath = Paths.get(zipFileName);
		if (!filePath.toFile().exists()) {
			throw new RuntimeException("The file was not found: " + filePath.getFileName().toString());
		}
		return filePath;
	}

	public Package savePackageInformation(Package packageInfo) {
		packageInfo.setPackageId(universalIdGenerator.generateUniversalId());
		packageInfo.setCreatedAt(new Date());
		List<Attachment> attachments = packageInfo.getAttachments();
		for (Attachment attachment : attachments) {
			attachment.setId(universalIdGenerator.generateUniversalId());
		}
		User user = userRepository.findByEmail(packageInfo.getSubmitter().getEmail());
		if (user == null) {
			user = userRepository.save(packageInfo.getSubmitter());
		}
		packageInfo.setSubmitter(user);
		Package savedPackage = packageRepository.save(packageInfo);
		return savedPackage;
	}

	public Package findPackage(String packageId) {
		return packageRepository.findByPackageId(packageId);
	}

	public void saveFile(MultipartFile file, String packageId, String filename, boolean shouldAppend) throws Exception {

		if (filename.equalsIgnoreCase("metadata.json")) {
			filename = filename.replace(".", "_user.");
		}

		packageFileHandler.saveMultipartFile(file, packageId, filename, shouldAppend);
	}

	public void createZipFile(String packageId) {

		Package packageInfo = packageRepository.findByPackageId(packageId);

		new Thread() {
			public void run() {
				try {
					packageZipper.createZipFile(packageInfo);
				} catch (IOException e) {
					e.printStackTrace();
				}
				log.info(zipPackage.format(new Object[] { "createZipFile", packageId }));
			}
		}.start();
	}

}
