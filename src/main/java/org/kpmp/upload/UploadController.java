package org.kpmp.upload;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {

	private UploadService uploadService;

	@Autowired
	public UploadController(UploadService uploadService) {
		this.uploadService = uploadService;
	}

	@RequestMapping(value = "/upload/packageInfo", consumes = { "application/json" }, method = RequestMethod.POST)
	public UploadPackageIds uploadPackageInfo(@RequestBody PackageInformation packageInformation) {
		UploadPackageIds ids = new UploadPackageIds();

		int uploadPackageId = uploadService.saveUploadPackage(packageInformation);
		ids.setPackageId(uploadPackageId);

		int submitterId = uploadService.saveSubmitterInfo(packageInformation);
		ids.setSubmitterId(submitterId);

		int institutionId = uploadService.findInstitutionId(packageInformation);
		ids.setInstitutionId(institutionId);

		return ids;
	}

	@RequestMapping(value = "/upload", consumes = { "multipart/form-data" }, method = RequestMethod.POST)
	public void handleFileUpload(@RequestParam("file") MultipartFile file,
			@RequestParam("fileMetadata") String fileMetadata, @RequestParam("packageId") int packageId,
			@RequestParam("submitterId") int submitterId, @RequestParam("institutionId") int institutionId)
			throws IllegalStateException, IOException {

		UploadPackageIds packageIds = new UploadPackageIds(packageId, submitterId, institutionId);
		uploadService.addFileToPackage(file, fileMetadata, packageIds);
	}

}
