package org.kpmp.upload;

import java.io.File;
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
	private FileHandler fileHandler;

	@Autowired
	public UploadController(UploadService uploadService, FileHandler fileHandler) {
		this.uploadService = uploadService;
		this.fileHandler = fileHandler;
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
	public String handleFileUpload(@RequestParam("qqfile") MultipartFile file,
			@RequestParam("fileMetadata") String fileMetadata, @RequestParam("packageId") int packageId,
			@RequestParam("submitterId") int submitterId, @RequestParam("institutionId") int institutionId,
			@RequestParam("qqfilename") String filename, @RequestParam("qqtotalparts") int chunks,
			@RequestParam("qqpartindex") int chunk) throws IllegalStateException, IOException {

		boolean fullFile = false;
		if ((chunk == 0 && chunks == 1)) {
			fullFile = true;
		}

		File savedFile = fileHandler.saveMultipartFile(file, packageId, filename, fullFile);

		if (chunk == chunks - 1) {
			UploadPackageIds packageIds = new UploadPackageIds(packageId, submitterId, institutionId);
			uploadService.addFileToPackage(savedFile, fileMetadata, packageIds);
		}
		return "{\"success\": " + true + "}";
	}

}
