package org.kpmp.packages;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import org.kpmp.UniversalIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PackageService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private static final MessageFormat zipPackage = new MessageFormat("Service|{0}|{1}");

	private PackageRepository packageRepository;
	private UniversalIdGenerator universalIdGenerator;
	private PackageFileHandler packageFileHandler;
	private PackageZipService packageZipper;

	@Autowired
	public PackageService(PackageRepository packageRepository, UniversalIdGenerator universalIdGenerator,
			PackageFileHandler packageFileHandler, PackageZipService packageZipper) {
		this.packageRepository = packageRepository;
		this.universalIdGenerator = universalIdGenerator;
		this.packageFileHandler = packageFileHandler;
		this.packageZipper = packageZipper;
	}

	public List<Package> findAllPackages() {
		return packageRepository.findAll();
	}

	public Package savePackageInformation(Package packageInfo) {
		packageInfo.setPackageId(universalIdGenerator.generateUniversalId());
		packageInfo.setCreatedAt(new Date());
		Package savedPackage = packageRepository.save(packageInfo);
		return savedPackage;
	}

	public Package findPackage(String packageId) {
		return packageRepository.findByPackageId(packageId);
	}

	public void saveFile(MultipartFile file, String packageId, String filename, long fileSize, boolean isInitialChunk)
			throws IOException {
		if (isInitialChunk) {
			updatePackageInfo(packageId, filename, fileSize);
		}
		packageFileHandler.saveMultipartFile(file, packageId, filename, !isInitialChunk);
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

	private void updatePackageInfo(String packageId, String filename, long fileSize) {
		Package packageInformation = packageRepository.findByPackageId(packageId);
		List<Attachment> attachments = packageInformation.getAttachments();
		Attachment attachment = createAttachment(filename, fileSize);
		attachments.add(attachment);
		packageInformation.setAttachments(attachments);
		packageRepository.save(packageInformation);
	}

	private Attachment createAttachment(String filename, long fileSize) {
		Attachment attachment = new Attachment();
		attachment.setFileName(filename);
		attachment.setSize(fileSize);
		attachment.setId(universalIdGenerator.generateUniversalId());
		return attachment;
	}

}
