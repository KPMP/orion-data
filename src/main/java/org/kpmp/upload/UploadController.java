package org.kpmp.upload;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpSession;

import org.kpmp.dao.FileMetadataEntries;
import org.kpmp.dao.FileSubmission;
import org.kpmp.dao.InstitutionDemographics;
import org.kpmp.dao.PackageTypeOther;
import org.kpmp.dao.SubmitterDemographics;
import org.kpmp.dao.UploadPackage;
import org.kpmp.dao.UploadPackageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private HttpSession session;
	private FilePathHelper filePathHelper;
	private MetadataHandler metadataHandler;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static final MessageFormat packageInfoRequest = new MessageFormat("Request|{0}|{1}");
	private static final MessageFormat fileUploadRequest = new MessageFormat("Request|{0}|{1}|{2}|{3}|{4}|{5}|{6}|{7}");

	private static final MessageFormat saveMetadata = new MessageFormat("metadata for package {0} created");

	@Autowired
	public UploadController(UploadService uploadService, FileHandler fileHandler, FilePathHelper filePathHelper, MetadataHandler metadataHandler) {
		this.uploadService = uploadService;
		this.fileHandler = fileHandler;
		this.filePathHelper = filePathHelper;
		this.metadataHandler = metadataHandler;
	}

	@RequestMapping(value = "/upload/packageInfo", consumes = { "application/json" }, method = RequestMethod.POST)
	public UploadPackageIds uploadPackageInfo(@RequestBody PackageInformation packageInformation, HttpSession httpSession) {

		session = httpSession;

		log.info(packageInfoRequest.format(new Object[] { "uploadPackageInfo", packageInformation }));
		UploadPackageIds ids = new UploadPackageIds();
		PackageTypeOther packageTypeOther = null;

		if ("Other".equals(packageInformation.getPackageType()) && ("".equals(packageInformation.getPackageTypeOther())
				|| packageInformation.getPackageTypeOther() == null)) {
			throw new IllegalArgumentException("Package type 'Other' selected, but not defined further.");
		}
		if ("Other".equals(packageInformation.getPackageType())) {
			packageTypeOther = uploadService.savePackageTypeOther(packageInformation.getPackageTypeOther());
		}

		int uploadPackageId = uploadService.saveUploadPackage(packageInformation, packageTypeOther);
		ids.setPackageId(uploadPackageId);

		int submitterId = uploadService.saveSubmitterInfo(packageInformation);
		ids.setSubmitterId(submitterId);

		int institutionId = uploadService.findInstitutionId(packageInformation);
		ids.setInstitutionId(institutionId);

		InstitutionDemographics institution = uploadService.findInstitution(packageInformation);
		SubmitterDemographics submitter = new SubmitterDemographics(packageInformation, new Date());
		UploadPackage uploadPackage = uploadService.createUploadPackage(packageInformation, packageTypeOther);
		uploadPackage.setFileSubmissions(new CopyOnWriteArrayList<FileSubmission>());

		session.setAttribute("institution", institution);
		session.setAttribute("submitter", submitter);
		session.setAttribute("uploadPackage", uploadPackage);
		session.setAttribute("ids", ids);

		return ids;

	}

	@RequestMapping(value = "/upload", consumes = { "multipart/form-data" }, method = RequestMethod.POST)
	public String handleFileUpload(@RequestParam("qqfile") MultipartFile file,
			@RequestParam("fileMetadata") String fileMetadataString, @RequestParam("packageId") int packageId,
			@RequestParam("submitterId") int submitterId, @RequestParam("institutionId") int institutionId,
			@RequestParam("fileId") int fileId, @RequestParam("totalFiles") int totalFiles,
			@RequestParam("qqfilename") String filename,
			@RequestParam(name = "qqtotalparts", defaultValue = "1") int chunks,
			@RequestParam(name = "qqpartindex", defaultValue = "0") int chunk) {

		log.info(fileUploadRequest.format(new Object[] { "handleFileUpload", filename, fileMetadataString, packageId,
				submitterId, institutionId, chunks, chunk }));
		boolean shouldAppend = false;
		if (chunk != 0) {
			shouldAppend = true;
		}

		Date createdDate = new Date();
		File savedFile;
		InstitutionDemographics institution = (InstitutionDemographics) session.getAttribute("institution");
		SubmitterDemographics submitter = (SubmitterDemographics) session.getAttribute("submitter");
		UploadPackage uploadPackage = (UploadPackage) session.getAttribute("uploadPackage");

		try {
			savedFile = fileHandler.saveMultipartFile(file, uploadPackage.getUniversalId(), filename, shouldAppend);
			if (chunk == chunks - 1) {
				UploadPackageIds packageIds = new UploadPackageIds(packageId, submitterId, institutionId);
				uploadService.addFileToPackage(savedFile, fileMetadataString, packageIds);
				FileMetadataEntries fileMetadata = new FileMetadataEntries();
				fileMetadata.setCreatedAt(createdDate);
				fileMetadata.setMetadata(fileMetadataString);

				CopyOnWriteArrayList<FileSubmission> fileSubmissions = new CopyOnWriteArrayList<>(uploadPackage.getFileSubmissions());

				FileSubmission fileSubmission = uploadService.createFileSubmission(savedFile, fileMetadata, institution, submitter, uploadPackage);
				fileSubmissions.add(fileSubmission);
				uploadPackage.setFileSubmissions(fileSubmissions);
				session.setAttribute("uploadPackage", uploadPackage);

				if (fileId + 1 == totalFiles) {
					String filePath = filePathHelper.getPackagePath("", uploadPackage.getUniversalId()) + filePathHelper.getMetadataFileName();
					UploadPackageMetadata uploadPackageMetadata = new UploadPackageMetadata(uploadPackage);
					metadataHandler.saveUploadPackageMetadata(uploadPackageMetadata, filePath);
					log.info(saveMetadata.format(new Object[]{uploadPackage.getId()}));
					session.invalidate();
				}
			}
		} catch (IOException e) {
			log.error("Unable to save multipart file with information: name: " + filename + " packageId: " + packageId,
					e);
			return "{\"success\": " + false + "}";
		}

		return "{\"success\": " + true + "}";
	}

	public HttpSession getSession() {
		return session;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}
}
