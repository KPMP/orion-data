package org.kpmp.packages;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.kpmp.UniversalIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PackageService {

	private PackageRepository packageRepository;
	private UniversalIdGenerator universalIdGenerator;
	private PackageFileHandler packageFileHandler;

	@Autowired
	public PackageService(PackageRepository packageRepository, UniversalIdGenerator universalIdGenerator,
			PackageFileHandler packageFileHandler) {
		this.packageRepository = packageRepository;
		this.universalIdGenerator = universalIdGenerator;
		this.packageFileHandler = packageFileHandler;
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
