package org.kpmp.packages;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.kpmp.dmd.DmdResponse;
import org.kpmp.dmd.DmdService;
import org.kpmp.logging.LoggingService;
import org.kpmp.shibboleth.ShibbolethUserService;
import org.kpmp.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class PackageController {

	@Value("${package.state.files.received}")
	private String filesReceivedState;
	@Value("${package.state.upload.started}")
	private String uploadStartedState;
	@Value("${package.state.metadata.received}")
	private String metadataReceivedState;
	@Value("${package.state.upload.failed}")
	private String uploadFailedState;

	@Value("${package.state.upload.succeeded}")
	private String uploadSucceededState;

	private static final MessageFormat finish = new MessageFormat("{0} {1}");
	private static final MessageFormat fileUploadRequest = new MessageFormat(
			"Posting file: {0} to package with id: {1}, filesize: {2}, chunk: {3} out of {4} chunks");

	private LoggingService logger;
	private PackageService packageService;
	private ShibbolethUserService shibUserService;
	private UniversalIdGenerator universalIdGenerator;

	private DmdService dmdService;

	@Autowired
	public PackageController(PackageService packageService, LoggingService logger,
			ShibbolethUserService shibUserService, UniversalIdGenerator universalIdGenerator, DmdService dmdService) {
		this.packageService = packageService;
		this.logger = logger;
		this.shibUserService = shibUserService;
		this.universalIdGenerator = universalIdGenerator;
		this.dmdService = dmdService;
	}

	@RequestMapping(value = "/v1/packages", params="shouldExclude", method = RequestMethod.GET)
	public @ResponseBody List<PackageView> getAllPackages( @RequestParam("shouldExclude") boolean shouldExclude, HttpServletRequest request)
			throws JSONException, IOException {
		
		if (shouldExclude) {
			logger.logInfoMessage(this.getClass(), null, "Request for filtered packages", request);
			return packageService.findMostPackages();
		} else {
			logger.logInfoMessage(this.getClass(), null, "Request for all packages", request);
			return packageService.findAllPackages();
		}
		
	}

	@RequestMapping(value = "/v1/packages", method = RequestMethod.POST)
	public @ResponseBody PackageResponse postPackageInformation(@RequestBody String packageInfoString,
			@RequestParam("hostname") String hostname, HttpServletRequest request) {
		String cleanHostName = hostname.replace("=", "");
		PackageResponse packageResponse = new PackageResponse();
		String packageId = universalIdGenerator.generateUniversalId();
		packageResponse.setPackageId(packageId);
		logger.logInfoMessage(this.getClass(), packageId, "setting package state", request);
		packageService.sendStateChangeEvent(packageId, uploadStartedState, null, cleanHostName);
		JSONObject packageInfo;
		try {
			packageInfo = new JSONObject(packageInfoString);
			logger.logInfoMessage(this.getClass(), packageId, "Posting package info: " + packageInfo, request);
			// Fake this until we get Shibboleth working
			// User user = shibUserService.getUserNoHeaders(request, packageInfo);
			User user = new User();
			user.setDisplayName("Test User");
			user.setShibId("test_user@test.com");
			user.setEmail("test_user@test.com");
			user.setFirstName("Test");
			user.setLastName("User");
			packageService.savePackageInformation(packageInfo, user, packageId);
			String largeFilesChecked = packageInfo.optBoolean("largeFilesChecked") ? "true" : "false";
			packageService.sendStateChangeEvent(packageId, metadataReceivedState, largeFilesChecked,
					packageResponse.getGlobusURL(), cleanHostName);
		} catch (Exception e) {
			logger.logErrorMessage(this.getClass(), packageId, e.getMessage(), request);
			packageService.sendStateChangeEvent(packageId, uploadFailedState, null, e.getMessage(), cleanHostName);
		}
		return packageResponse;
	}

	@RequestMapping(value = "/v1/packages/{packageId}/files", method = RequestMethod.POST, consumes = {
			"multipart/form-data" })
	public @ResponseBody FileUploadResponse postFilesToPackage(@PathVariable("packageId") String packageId,
			@RequestParam("qqfile") MultipartFile file, 
            @RequestParam("qqfilename") String filename,
			@RequestParam("qqtotalfilesize") long fileSize,
			@RequestParam(name = "qqtotalparts", defaultValue = "1") int chunks,
			@RequestParam(name = "qqpartindex", defaultValue = "0") int chunk, HttpServletRequest request) {

        Package packageInfo = packageService.findPackage(packageId);
        String study = packageInfo.getStudy();
		String hostname = request.getHeader("Host");
		String fileRename = findFileRename(packageInfo.getAttachments(), filename);
		String cleanHostName = hostname.replace("=", "");
		if (fileRename == null) {
			logger.logErrorMessage(this.getClass(), packageId, "Unable to find file rename for file: " + filename, request);
			packageService.sendStateChangeEvent(packageId, uploadFailedState, null, "Unable to find file rename", cleanHostName);
			return new FileUploadResponse(false);
		}

		String message = fileUploadRequest.format(new Object[] { fileRename, packageId, fileSize, chunk, chunks });
		logger.logInfoMessage(this.getClass(), packageId, message, request);

		try {
			packageService.saveFile(file, packageId, fileRename, study, shouldAppend(chunk));
		} catch (Exception e) {
			logger.logErrorMessage(this.getClass(), packageId, e.getMessage(), request);
			packageService.sendStateChangeEvent(packageId, uploadFailedState, null, e.getMessage(), cleanHostName);
			return new FileUploadResponse(false);
		}

		return new FileUploadResponse(true);
	}


	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/v1/packages/{packageId}/files/move", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity movePackageFiles(@PathVariable String packageId, HttpServletRequest request) {
		ResponseEntity responseEntity;
		DmdResponse dmdResponse;
		try {
			logger.logInfoMessage(this.getClass(), packageId, "Moving files for package " + packageId, request);
			dmdResponse = dmdService.moveFiles(packageId);
			if (dmdResponse.isSuccess()) {
				String successMessage = "The following files were moved successfully: " + String.join(",", dmdResponse.getFileNameList());
				logger.logInfoMessage(this.getClass(), packageId, successMessage, request);
				responseEntity = ResponseEntity.ok().body(successMessage);
			} else {
				logger.logErrorMessage(this.getClass(), packageId, dmdResponse.getMessage(), request);
				responseEntity = ResponseEntity.status(INTERNAL_SERVER_ERROR).body("The following problem occurred while moving the files: " + dmdResponse.getMessage());
			}
		} catch (IOException e) {
			logger.logErrorMessage(this.getClass(), packageId, e.getMessage(), request);
			responseEntity = ResponseEntity.status(INTERNAL_SERVER_ERROR).body("There was a server error while moving the files.");
		}
		return responseEntity;
	}

	@RequestMapping(value = "/v1/packages/{packageId}/files/finish", method = RequestMethod.POST)
	public @ResponseBody FileUploadResponse finishUpload(@PathVariable("packageId") String packageId,
			@RequestBody String hostname, HttpServletRequest request) {

		String cleanHostName = hostname.replace("=", "");
		packageService.sendStateChangeEvent(packageId, filesReceivedState, null, cleanHostName);
		FileUploadResponse fileUploadResponse;
		String message = finish.format(new Object[] { "Finishing file upload with packageId: ", packageId });
		logger.logInfoMessage(this.getClass(), packageId, message, request);
		if (packageService.validatePackage(packageId, shibUserService.getUser(request))) {
			try {
				//packageService.calculateAndSaveChecksums(packageId);
				fileUploadResponse = new FileUploadResponse(true);
				packageService.sendStateChangeEvent(packageId, uploadSucceededState, null, cleanHostName);
			} catch (Exception e) {
				String errorMessage = finish
						.format(new Object[] { "There was a problem calculating the checksum for package ", packageId });
				logger.logErrorMessage(this.getClass(), packageId, errorMessage, request);
				fileUploadResponse = new FileUploadResponse(false);
				packageService.sendStateChangeEvent(packageId, uploadFailedState, null, errorMessage, cleanHostName);
			}
		} else {
			String errorMessage = finish.format(new Object[] { "The files on disk did not match the database: ", packageId });
			logger.logErrorMessage(this.getClass(), packageId, errorMessage, request);
			fileUploadResponse = new FileUploadResponse(false);
			packageService.sendStateChangeEvent(packageId, uploadFailedState, null, errorMessage, cleanHostName);
		}
		return fileUploadResponse;
	}

	private boolean shouldAppend(int chunk) {
		return chunk != 0;
	}

	private String findFileRename (List<Attachment> attachments, String originalFileName) {
		for (Attachment attachment : attachments) {
			if (attachment.getOriginalFileName().equalsIgnoreCase(originalFileName)) {
				return attachment.getFileName();
			}
		}
		return null;
	}

}
