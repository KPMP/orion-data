package org.kpmp.upload;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.kpmp.dao.FileMetadataEntries;
import org.kpmp.dao.FileSubmissions;
import org.kpmp.dao.InstitutionDemographics;
import org.kpmp.dao.SubmitterDemographics;
import org.kpmp.dao.UploadPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadService {

	@Value("${file.base.path}")
	private String basePath;
	private UploadPackageRepository uploadPackageRepository;
	private FileSubmissionsRepository fileSubmissionsRepository;
	private SubmitterRepository submitterRepository;
	private InstitutionRepository institutionRepository;

	@Autowired
	public UploadService(UploadPackageRepository uploadPackageRepository,
			FileSubmissionsRepository fileSubmissionsRepository, SubmitterRepository submitterRepository,
			InstitutionRepository institutionRepository) {
		this.uploadPackageRepository = uploadPackageRepository;
		this.fileSubmissionsRepository = fileSubmissionsRepository;
		this.submitterRepository = submitterRepository;
		this.institutionRepository = institutionRepository;

	}

	public int saveUploadPackage(PackageInformation packageInfo) {
		UploadPackage uploadPackage = new UploadPackage(packageInfo, new Date());
		UploadPackage savedPackage = uploadPackageRepository.save(uploadPackage);
		return savedPackage.getId();
	}

	public int saveSubmitterInfo(PackageInformation packageInformation) {
		SubmitterDemographics submitter = new SubmitterDemographics(packageInformation, new Date());
		SubmitterDemographics savedSubmitter = submitterRepository.save(submitter);
		return savedSubmitter.getId();
	}

	public int findInstitutionId(PackageInformation packageInformation) {
		InstitutionDemographics institution = institutionRepository
				.findByInstitutionName(packageInformation.getInstitutionName());
		return institution.getId();
	}

	public void addFileToPackage(MultipartFile file, String fileMetadataString, UploadPackageIds packageIds)
			throws IllegalStateException, IOException {
		Date createdDate = new Date();

		UploadPackage uploadPackage = uploadPackageRepository.findById(packageIds.getPackageId());
		SubmitterDemographics submitter = submitterRepository.findById(packageIds.getSubmitterId());
		InstitutionDemographics institution = institutionRepository.findById(packageIds.getInstitutionId());

		File packageDirectory = new File(basePath + File.separator + "package" + packageIds.getPackageId());
		if (!packageDirectory.exists()) {
			packageDirectory.mkdirs();
		}

		File fileToSave = new File(
				basePath + File.separator + "package" + packageIds.getPackageId() + File.separator + file.getName());
		// check to see if package directory exists, if not create it
		file.transferTo(fileToSave);

		FileMetadataEntries fileMetadata = new FileMetadataEntries();
		fileMetadata.setCreatedAt(createdDate);
		fileMetadata.setMetadata(fileMetadataString);

		FileSubmissions fileSubmission = new FileSubmissions();
		fileSubmission.setCreatedAt(createdDate);
		fileSubmission.setFilename(file.getOriginalFilename());
		fileSubmission.setFileSize(file.getSize());
		fileSubmission.setFileMetadata(fileMetadata);
		fileSubmission.setFilePath(fileToSave.getPath());
		fileSubmission.setInstitution(institution);
		fileSubmission.setSubmitter(submitter);
		fileSubmission.setUploadPackage(uploadPackage);

		fileSubmissionsRepository.save(fileSubmission);

	}

}
