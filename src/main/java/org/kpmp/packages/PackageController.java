package org.kpmp.packages;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.kpmp.logging.LoggingService;
import org.kpmp.shibboleth.ShibbolethUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

	private PackageService packageService;

	private static final MessageFormat finish = new MessageFormat("{0} {1}");
	private static final MessageFormat fileUploadRequest = new MessageFormat(
			"Posting file: {0} to package with id: {1}, filesize: {2}, chunk: {3} out of {4} chunks");
	private static final MessageFormat fileDownloadRequest = new MessageFormat(
			"Requesting package download with id {0}, filename {1}");
	private LoggingService logger;

	private ShibbolethUserService shibUserService;

	@Autowired
	public PackageController(PackageService packageService, LoggingService logger,
			ShibbolethUserService shibUserService) {
		this.packageService = packageService;
		this.logger = logger;
		this.shibUserService = shibUserService;
	}

	@RequestMapping(value = "/v1/packages", method = RequestMethod.GET)
	public @ResponseBody List<PackageView> getAllPackages(HttpServletRequest request)
			throws JSONException, IOException {
		logger.logInfoMessage(this.getClass(), null, "Request for all packages", request);
		return packageService.findAllPackages();
	}

	@RequestMapping(value = "/v1/packages", method = RequestMethod.POST)
	public @ResponseBody String postPackageInformation(@RequestBody String packageInfoString,
			HttpServletRequest request) throws JSONException {
		JSONObject packageInfo = new JSONObject(packageInfoString);
		logger.logInfoMessage(this.getClass(), null, "Posting package info: " + packageInfo, request);
		String packageId = packageService.savePackageInformation(packageInfo);
		return packageId;
	}

	@RequestMapping(value = "/v1/packages/{packageId}/files", method = RequestMethod.POST, consumes = {
			"multipart/form-data" })
	public @ResponseBody FileUploadResponse postFilesToPackage(@PathVariable("packageId") String packageId,
			@RequestParam("qqfile") MultipartFile file, @RequestParam("qqfilename") String filename,
			@RequestParam("qqtotalfilesize") long fileSize,
			@RequestParam(name = "qqtotalparts", defaultValue = "1") int chunks,
			@RequestParam(name = "qqpartindex", defaultValue = "0") int chunk, HttpServletRequest request)
			throws Exception {

		String message = fileUploadRequest.format(new Object[] { filename, packageId, fileSize, chunk, chunks });
		logger.logInfoMessage(this.getClass(), packageId, message, request);

		packageService.saveFile(file, packageId, filename, shouldAppend(chunk));

		return new FileUploadResponse(true);
	}

	@RequestMapping(value = "/v1/packages/{packageId}/files", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Resource> downloadPackage(@PathVariable String packageId,
			HttpServletRequest request) {
		Resource resource = null;
		try {
			resource = new UrlResource(packageService.getPackageFile(packageId).toUri());
		} catch (Exception e) {
			logger.logErrorMessage(this.getClass(), packageId, "Unable to get package zip with id: " + packageId,
					request);
			throw new RuntimeException(e);
		}
		String message = fileDownloadRequest.format(new Object[] { packageId, resource.toString() });
		logger.logInfoMessage(this.getClass(), packageId, message, request);

		return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	@RequestMapping(value = "/v1/packages/{packageId}/files/finish", method = RequestMethod.POST)
	public @ResponseBody FileUploadResponse finishUpload(@PathVariable("packageId") String packageId,
			@RequestBody String hostname, HttpServletRequest request) throws UnsupportedEncodingException {

		FileUploadResponse fileUploadResponse;
		String message = finish.format(new Object[] { "Finishing file upload with packageId: ", packageId });
		logger.logInfoMessage(this.getClass(), packageId, message, request);
		if (packageService.validatePackageForZipping(packageId, shibUserService.getUser(request))) {
			try {
				String removeErrantEqualSign = hostname.replace("=", "");
				packageService.createZipFile(packageId, removeErrantEqualSign);

				fileUploadResponse = new FileUploadResponse(true);
			} catch (Exception e) {
				logger.logErrorMessage(this.getClass(), packageId,
						finish.format(new Object[] { "error getting metadata for package id: ", packageId }), request);
				fileUploadResponse = new FileUploadResponse(false);
			}
		} else {
			logger.logErrorMessage(this.getClass(), packageId,
					finish.format(new Object[] { "Unable to zip package with package id: ", packageId }), request);
			fileUploadResponse = new FileUploadResponse(false);
		}
		return fileUploadResponse;
	}

	private boolean shouldAppend(int chunk) {
		return chunk != 0;
	}

}
