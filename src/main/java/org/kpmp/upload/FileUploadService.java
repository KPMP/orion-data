package org.kpmp.upload;

import java.io.File;
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
public class FileUploadService {

	@Value("${file.base.path}")
	private String basePath;
	private UploadPackageRepository uploadPackageRepository;
	private FileSubmissionsRepository fileSubmissionsRepository;

	@Autowired
	public FileUploadService(UploadPackageRepository uploadPackageRepository,
			FileSubmissionsRepository fileSubmissionsRepository) {
		this.uploadPackageRepository = uploadPackageRepository;
		this.fileSubmissionsRepository = fileSubmissionsRepository;

	}

	public void uploadPackage(MultipartFile file, PackageInformation packageInfo, String fileMetadataString) {
		Date createdDate = new Date();

		UploadPackage uploadPackage = new UploadPackage();
		uploadPackage.setCreatedAt(createdDate);
		uploadPackage.setExperimentDate(packageInfo.getExperimentDate());
		uploadPackage.setExperimentId(packageInfo.getExperimentId());
		uploadPackage.setSubjectId(packageInfo.getSubjectId());
		UploadPackage updatedPackage = uploadPackageRepository.save(uploadPackage);

		File fileToSave = new File(
				basePath + File.separator + "package" + updatedPackage.getId() + File.separator + file.getName());
		try {
			file.transferTo(fileToSave);
			FileMetadataEntries fileMetadata = new FileMetadataEntries();
			fileMetadata.setCreatedAt(createdDate);
			fileMetadata.setMetadata(fileMetadataString);

			// I think we are actually going to want to look up the institution,
			// but
			// I am creating a new one
			// here to make sure mappings are working correctly.
			InstitutionDemographics institution = new InstitutionDemographics();
			institution.setInstitutionName(packageInfo.getInstitutionName());

			SubmitterDemographics submitter = new SubmitterDemographics();
			submitter.setCreatedAt(createdDate);
			submitter.setFirstName(packageInfo.getFirstName());
			submitter.setLastName(packageInfo.getLastName());

			FileSubmissions fileSubmission = new FileSubmissions();
			fileSubmission.setCreatedAt(createdDate);
			fileSubmission.setFilename(file.getOriginalFilename());
			fileSubmission.setFileSize(file.getSize());
			fileSubmission.setFileMetadata(fileMetadata);
			fileSubmission.setInstitution(institution);
			fileSubmission.setSubmitter(submitter);
			fileSubmission.setUploadPackage(uploadPackage);

			fileSubmissionsRepository.save(fileSubmission);
		} catch (Exception e) {
			uploadPackageRepository.delete(updatedPackage);
			e.printStackTrace();
		}

	}

}
