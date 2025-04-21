package org.miktmc.packages;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.miktmc.logging.LoggingService;
import org.miktmc.shibboleth.ShibbolethUserService;
import org.miktmc.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	@Value("${package.state.upload.locked}")
	private String uploadLockedState;
	@Value("${package.state.upload.succeeded}")
	private String uploadSucceededState;

	private static final MessageFormat finish = new MessageFormat("{0} {1}");
	private static final MessageFormat fileUploadRequest = new MessageFormat(
			"Posting file: {0} to package with id: {1}, filesize: {2}, chunk: {3} out of {4} chunks");

	private LoggingService logger;
	private PackageService packageService;
	private ShibbolethUserService shibUserService;
	private UniversalIdGenerator universalIdGenerator;


	@Autowired
	public PackageController(PackageService packageService, LoggingService logger,
			ShibbolethUserService shibUserService, UniversalIdGenerator universalIdGenerator) {
		this.packageService = packageService;
		this.logger = logger;
		this.shibUserService = shibUserService;
		this.universalIdGenerator = universalIdGenerator;
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
			User user = shibUserService.getUserNoHeaders(request, packageInfo);
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

	@RequestMapping(value = "/v1/packages/{packageId}/files/add", method = RequestMethod.POST)
	public @ResponseBody List<Attachment> postNewFiles(@PathVariable("packageId") String packageId, @RequestBody String packageInfoString,
													   @RequestParam("hostname") String hostname, HttpServletRequest request) {
		JSONObject packageInfo;
		HttpSession session = request.getSession(false);
		String shibId = "";
		List<Attachment> files = null;
		if (session != null) {
			shibId = (String)session.getAttribute("shibid");
		}
		try {
			packageInfo = new JSONObject(packageInfoString);
			JSONArray jsonFiles = packageInfo.getJSONArray("files");
			files = packageService.addFiles(packageId, jsonFiles, shibId, false);
            packageService.stripMetadata(packageService.findPackage(packageId));
		} catch (Exception e) {
			logger.logErrorMessage(this.getClass(), packageId, e.getMessage(), request);
			packageService.sendStateChangeEvent(packageId, uploadFailedState, null, e.getMessage(), hostname);
		}
		return files;
	}

	@RequestMapping(value = "/v1/packages/{packageId}/files/replace/{fileId}", method = RequestMethod.POST)
	public @ResponseBody FileUploadResponse replaceFile(@PathVariable("packageId") String packageId, @PathVariable("fileId") String fileId, @RequestBody String packageInfoString,
													   @RequestParam("hostname") String hostname, HttpServletRequest request) {
		JSONObject packageInfo;
		HttpSession session = request.getSession(false);
		String shibId = "";
		if (session != null) {
			shibId = (String)session.getAttribute("shibid");
		}
		FileUploadResponse response = new FileUploadResponse(false);
		packageInfo = new JSONObject(packageInfoString);
		JSONArray jsonFiles = packageInfo.getJSONArray("files");
		if (!jsonFiles.isEmpty()) {
            JSONObject file = jsonFiles.getJSONObject(0);
			String originalFileName = file.getString(PackageKeys.FILE_NAME.getKey());
			boolean didDelete = false;
			if (packageService.canReplaceFile(packageId, fileId, originalFileName)) {
				didDelete = packageService.deleteFile(packageId, fileId, shibId);
			}
			if (didDelete) {
				try {
					packageService.addFiles(packageId, jsonFiles, shibId, true);
                    packageService.stripMetadata(packageService.findPackage(packageId));
					response.setSuccess(true);
				} catch (Exception e) {
					logger.logErrorMessage(this.getClass(), packageId, e.getMessage(), request);
				}
			}
        }

		return response;
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
			if (e.getClass().getName() != FileAlreadyExistsException.class.getName()) {
				packageService.sendStateChangeEvent(packageId, uploadFailedState, null, e.getMessage(), cleanHostName);
			}
			logger.logErrorMessage(this.getClass(), packageId, "Unable to save file. " + e.getMessage(), request);
			return new FileUploadResponse(false);
		}

		return new FileUploadResponse(true);
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
				packageService.setPackageValidated(packageId);
                packageService.stripMetadata(packageService.findPackage(packageId));
				fileUploadResponse = new FileUploadResponse(true);
				packageService.sendStateChangeEvent(packageId, uploadSucceededState, null, cleanHostName);
			} catch (Exception e) {
				String errorMessage = finish
						.format(new Object[] { "There was a problem calculating the checksum for package ", packageId });
				logger.logErrorMessage(this.getClass(), packageId, errorMessage, request);
                e.printStackTrace();
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

	@RequestMapping(value = "/v1/packages/{packageId}/lock", method = RequestMethod.POST)
	public @ResponseBody boolean lockPackage(@PathVariable("packageId") String packageId, @RequestBody String hostname, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		String shibId = "";
		if (session != null) {
			shibId = (String)session.getAttribute("shibid");
		}

		String cleanHostName = hostname.replace("=", "");
		packageService.sendStateChangeEvent(packageId, uploadLockedState, null, "Locked by ["+ shibId + "]", cleanHostName);
		return true;
	}

	@RequestMapping(value = "/v1/packages/{packageId}/files/delete/{fileId}", method = RequestMethod.POST)
	public @ResponseBody boolean deletePackage(@PathVariable("packageId") String packageId, @PathVariable("fileId") String fileId, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		String shibId = "";
		if (session != null) {
			shibId = (String)session.getAttribute("shibid");
		}
		return packageService.deleteFile(packageId, fileId, shibId);
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
