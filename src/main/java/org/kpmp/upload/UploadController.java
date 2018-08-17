package org.kpmp.upload;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpSession;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
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
import org.springframework.web.bind.annotation.PathVariable;
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
	private static final MessageFormat finishRequest = new MessageFormat("Request|{0}|{1}");

	private static final MessageFormat saveMetadata = new MessageFormat("metadata for package {0} created");
	private UploadPackageRepository uploadPackageRepository;

	@Autowired
	public UploadController(UploadService uploadService, FileHandler fileHandler, FilePathHelper filePathHelper,
			MetadataHandler metadataHandler, UploadPackageRepository uploadPackageRepository) {
		this.uploadService = uploadService;
		this.fileHandler = fileHandler;
		this.filePathHelper = filePathHelper;
		this.metadataHandler = metadataHandler;
		this.uploadPackageRepository = uploadPackageRepository;
	}

	@RequestMapping(value = "/upload/packageInfo", consumes = { "application/json" }, method = RequestMethod.POST)
	public UploadPackageIds uploadPackageInfo(@RequestBody PackageInformation packageInformation,
			HttpSession httpSession) {

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

	@RequestMapping(value = "/upload/finish/{packageId}", method = RequestMethod.POST)
	public String finishUpload(@PathVariable int packageId) throws IOException {
		log.info(finishRequest.format(new Object[] { "finishUpload", packageId }));
		UploadPackage uploadPackage = uploadPackageRepository.findById(packageId);
		generateMetadataFile(packageId, uploadPackage);
		new Thread() {
			public void run() {
				try {
					createZip(uploadPackage.getFileSubmissions(), packageId, uploadPackage.getUniversalId());
				} catch (IOException e) {
					e.printStackTrace();
				}
				log.info(finishRequest.format(new Object[] { "finishZip", packageId }));
			}
		}.start();
		return "{\"success\": " + true + "}";
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
		if (chunks != 0) {
			shouldAppend = true;
		}

		Date createdDate = new Date();
		File savedFile;
		InstitutionDemographics institution = (InstitutionDemographics) session.getAttribute("institution");
		SubmitterDemographics submitter = (SubmitterDemographics) session.getAttribute("submitter");
		UploadPackage uploadPackage = (UploadPackage) session.getAttribute("uploadPackage");

		try {
			savedFile = fileHandler.saveMultipartFile(file, packageId, filename, shouldAppend);
			if (chunk == chunks - 1) {
				UploadPackageIds packageIds = new UploadPackageIds(packageId, submitterId, institutionId);
				uploadService.addFileToPackage(savedFile, fileMetadataString, packageIds);
				FileMetadataEntries fileMetadata = createFileMetadata(fileMetadataString, createdDate);

				CopyOnWriteArrayList<FileSubmission> fileSubmissions = new CopyOnWriteArrayList<>(
						uploadPackage.getFileSubmissions());

				FileSubmission fileSubmission = uploadService.createFileSubmission(savedFile, fileMetadata, institution,
						submitter, uploadPackage);
				fileSubmissions.add(fileSubmission);
				uploadPackage.setFileSubmissions(fileSubmissions);
				session.setAttribute("uploadPackage", uploadPackage);

			}
		} catch (IOException e) {
			log.error("Unable to save multipart file with information: name: " + filename + " packageId: " + packageId,
					e);
			return "{\"success\": " + false + "}";
		}

		return "{\"success\": " + true + "}";
	}

	private void createZip(List<FileSubmission> files, int packageId, String universalId) throws IOException {
		String zipFileName = filePathHelper.getPackagePath("", Integer.toString(packageId)) + universalId + ".zip";
		File zipFileTempHandle = new File(zipFileName + ".tmp");
		File zipFileHandle = new File(zipFileName);
		ZipArchiveOutputStream zipFile = new ZipArchiveOutputStream(zipFileTempHandle);
		zipFile.setMethod(ZipArchiveOutputStream.DEFLATED);
		zipFile.setEncoding("UTF-8");

		for (FileSubmission fileSubmission : files) {
			File file = new File(fileSubmission.getFilePath());
			ZipArchiveEntry entry = new ZipArchiveEntry(file.getName());
			entry.setSize(fileSubmission.getFileSize());
			zipFile.putArchiveEntry(entry);
			FileInputStream fileInputStream = new FileInputStream(file);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
			byte [] buffer = new byte[32768];
			int data = bufferedInputStream.read(buffer);
			while (data != -1) {
				zipFile.write(buffer);
				data = bufferedInputStream.read(buffer);
			}
			fileInputStream.close();
			bufferedInputStream.close();
			zipFile.flush();
			zipFile.closeArchiveEntry();
		}
		File metadataFile = new File(
				filePathHelper.getPackagePath("", Integer.toString(packageId)) + filePathHelper.getMetadataFileName());
		ZipArchiveEntry entry = new ZipArchiveEntry(metadataFile.getName());
		entry.setSize(metadataFile.length());
		zipFile.putArchiveEntry(entry);
		zipFile.write(IOUtils.toByteArray(new FileInputStream(metadataFile)));
		zipFile.closeArchiveEntry();
		zipFile.close();
		zipFileTempHandle.renameTo(zipFileHandle);
	}

	private void generateMetadataFile(int packageId, UploadPackage uploadPackage) throws IOException {
		String filePath = filePathHelper.getPackagePath("", Integer.toString(packageId))
				+ filePathHelper.getMetadataFileName();
		UploadPackageMetadata uploadPackageMetadata = new UploadPackageMetadata(uploadPackage);
		metadataHandler.saveUploadPackageMetadata(uploadPackageMetadata, filePath);
		log.info(saveMetadata.format(new Object[] { uploadPackage.getId() }));
	}

	private FileMetadataEntries createFileMetadata(String fileMetadataString, Date createdDate) {
		FileMetadataEntries fileMetadata = new FileMetadataEntries();
		fileMetadata.setCreatedAt(createdDate);
		fileMetadata.setMetadata(fileMetadataString);
		return fileMetadata;
	}

	public HttpSession getSession() {
		return session;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

}
