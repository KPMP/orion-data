package org.kpmp.upload.deprecated;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.kpmp.UniversalIdGenerator;
import org.kpmp.dao.deprecated.FileMetadataEntries;
import org.kpmp.dao.deprecated.FileSubmission;
import org.kpmp.dao.deprecated.FileSubmissionsRepository;
import org.kpmp.dao.deprecated.InstitutionDemographics;
import org.kpmp.dao.deprecated.PackageType;
import org.kpmp.dao.deprecated.PackageTypeOther;
import org.kpmp.dao.deprecated.Protocol;
import org.kpmp.dao.deprecated.SubmitterDemographics;
import org.kpmp.dao.deprecated.UploadPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UploadService {

	private UploadPackageMySQLRepository uploadPackageRepository;
	private FileSubmissionsRepository fileSubmissionsRepository;
	private SubmitterRepository submitterRepository;
	private InstitutionRepository institutionRepository;
	private FileMetadataRepository fileMetadataRepository;
	private PackageTypeRepository packageTypeRepository;
	private PackageTypeOtherRepository packageTypeOtherRepository;
	private UniversalIdGenerator uuidGenerator;
	private ProtocolRepository protocolRepository;

	@Autowired
	public UploadService(UploadPackageMySQLRepository uploadPackageRepository,
			FileSubmissionsRepository fileSubmissionsRepository, SubmitterRepository submitterRepository,
			InstitutionRepository institutionRepository, FileMetadataRepository fileMetadataRepository,
			PackageTypeRepository packageTypeRepository, PackageTypeOtherRepository packageTypeOtherRepository,
			UniversalIdGenerator uuidGenerator, ProtocolRepository protocolRepository) {
		this.uploadPackageRepository = uploadPackageRepository;
		this.fileSubmissionsRepository = fileSubmissionsRepository;
		this.submitterRepository = submitterRepository;
		this.institutionRepository = institutionRepository;
		this.fileMetadataRepository = fileMetadataRepository;
		this.packageTypeRepository = packageTypeRepository;
		this.packageTypeOtherRepository = packageTypeOtherRepository;
		this.uuidGenerator = uuidGenerator;
		this.protocolRepository = protocolRepository;
	}

	public PackageTypeOther savePackageTypeOther(String packageTypeOtherValue) {
		PackageTypeOther packageTypeOther = new PackageTypeOther();
		packageTypeOther.setPackageType(packageTypeOtherValue);
		return packageTypeOtherRepository.save(packageTypeOther);
	}

	public int saveUploadPackage(PackageInformation packageInfo, PackageTypeOther packageTypeOther) {
		UploadPackage uploadPackage = createUploadPackage(packageInfo, packageTypeOther);
		UploadPackage savedPackage = uploadPackageRepository.save(uploadPackage);
		return savedPackage.getId();
	}

	public UploadPackage createUploadPackage(PackageInformation packageInfo, PackageTypeOther packageTypeOther) {
		PackageType packageType = packageTypeRepository.findByPackageType(packageInfo.getPackageType());
		Protocol protocol = protocolRepository.findByProtocol(packageInfo.getProtocol());
		UploadPackage uploadPackage = new UploadPackage(packageInfo, new Date());
		uploadPackage.setPackageTypeOther(packageTypeOther);
		uploadPackage.setPackageType(packageType);
		uploadPackage.setUniversalId(uuidGenerator.generateUniversalId());
		uploadPackage.setProtocol(protocol);
		return uploadPackage;
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

	public InstitutionDemographics findInstitution(PackageInformation packageInformation) {
		return institutionRepository.findByInstitutionName(packageInformation.getInstitutionName());
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

		FileSubmission fileSubmission = createFileSubmission(file, savedMetadata, institution, submitter,
				uploadPackage);

		fileSubmissionsRepository.save(fileSubmission);

	}

	public FileSubmission createFileSubmission(File file, FileMetadataEntries fileMetadata,
			InstitutionDemographics institution, SubmitterDemographics submitter, UploadPackage uploadPackage)
			throws IllegalStateException, IOException {
		Date createdDate = new Date();

		FileSubmission fileSubmission = new FileSubmission();
		fileSubmission.setUniversalId(uuidGenerator.generateUniversalId());
		fileSubmission.setCreatedAt(createdDate);
		fileSubmission.setFilename(file.getName());
		fileSubmission.setFileSize(file.length());
		fileSubmission.setFilePath(file.getPath());
		fileSubmission.setFileMetadata(fileMetadata);
		fileSubmission.setInstitution(institution);
		fileSubmission.setSubmitter(submitter);
		fileSubmission.setUploadPackage(uploadPackage);

		return fileSubmission;

	}

}
