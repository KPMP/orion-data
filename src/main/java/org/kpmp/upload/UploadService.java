package org.kpmp.upload;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.kpmp.dao.FileMetadataEntries;
import org.kpmp.dao.FileSubmission;
import org.kpmp.dao.FileSubmissionsRepository;
import org.kpmp.dao.InstitutionDemographics;
import org.kpmp.dao.PackageType;
import org.kpmp.dao.PackageTypeOther;
import org.kpmp.dao.SubmitterDemographics;
import org.kpmp.dao.UploadPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UploadService {

	private UploadPackageRepository uploadPackageRepository;
	private FileSubmissionsRepository fileSubmissionsRepository;
	private SubmitterRepository submitterRepository;
	private InstitutionRepository institutionRepository;
	private FileMetadataRepository fileMetadataRepository;
	private PackageTypeRepository packageTypeRepository;
	private PackageTypeOtherRepository packageTypeOtherRepository;

	@Autowired
	public UploadService(UploadPackageRepository uploadPackageRepository,
			FileSubmissionsRepository fileSubmissionsRepository, SubmitterRepository submitterRepository,
			InstitutionRepository institutionRepository, FileMetadataRepository fileMetadataRepository,
			PackageTypeRepository packageTypeRepository, PackageTypeOtherRepository packageTypeOtherRepository) {
		this.uploadPackageRepository = uploadPackageRepository;
		this.fileSubmissionsRepository = fileSubmissionsRepository;
		this.submitterRepository = submitterRepository;
		this.institutionRepository = institutionRepository;
		this.fileMetadataRepository = fileMetadataRepository;
		this.packageTypeRepository = packageTypeRepository;
		this.packageTypeOtherRepository = packageTypeOtherRepository;

	}

	public PackageTypeOther savePackageTypeOther(String packageTypeOtherValue) {
		PackageTypeOther packageTypeOther = new PackageTypeOther();
		packageTypeOther.setPackageType(packageTypeOtherValue);
		return packageTypeOtherRepository.save(packageTypeOther);
	}

	public int saveUploadPackage(PackageInformation packageInfo) {
		PackageType packageType = packageTypeRepository.findByPackageType(packageInfo.getPackageType());
		UploadPackage uploadPackage = new UploadPackage(packageInfo, new Date());
		uploadPackage.setPackageType(packageType);
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

	public void addFileToPackage(File file, String fileMetadataString, UploadPackageIds packageIds)
			throws IllegalStateException, IOException {
		Date createdDate = new Date();

		UploadPackage uploadPackage = uploadPackageRepository.findById(packageIds.getPackageId());
		SubmitterDemographics submitter = submitterRepository.findById(packageIds.getSubmitterId());
		InstitutionDemographics institution = institutionRepository.findById(packageIds.getInstitutionId());

		FileMetadataEntries fileMetadata = new FileMetadataEntries();
		fileMetadata.setCreatedAt(createdDate);
		fileMetadata.setMetadata(fileMetadataString);
		FileMetadataEntries savedMetadata = fileMetadataRepository.save(fileMetadata);

		FileSubmission fileSubmission = new FileSubmission();
		fileSubmission.setCreatedAt(createdDate);
		fileSubmission.setFilename(file.getName());
		fileSubmission.setFileSize(file.length());
		fileSubmission.setFilePath(file.getPath());
		fileSubmission.setFileMetadata(savedMetadata);
		fileSubmission.setInstitution(institution);
		fileSubmission.setSubmitter(submitter);
		fileSubmission.setUploadPackage(uploadPackage);

		fileSubmissionsRepository.save(fileSubmission);

	}

}
