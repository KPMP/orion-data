package org.kpmp.packages;

import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static final MessageFormat packageInfoPost = new MessageFormat("Request|{0}|{1}");
	private static final MessageFormat finish = new MessageFormat("Request|{0}|{1}");
	private static final MessageFormat fileUploadRequest = new MessageFormat("Request|{0}|{1}|{2}|{3}|{4}|{5}");
	private static final MessageFormat fileDownloadRequest = new MessageFormat("Request|{0}|{1}");

	@Autowired
	public PackageController(PackageService packageService) {
		this.packageService = packageService;

	}

	@RequestMapping(value = "/v1/packages", method = RequestMethod.GET)
	public @ResponseBody List<PackageView> getAllPackages() {
		return packageService.findAllPackages();
	}

	@RequestMapping(value = "/v1/packages", method = RequestMethod.POST)
	public @ResponseBody String postPackageInformation(@RequestBody Package packageInfo) {
		log.info(packageInfoPost.format(new Object[] { "postPackageInfo", packageInfo }));

		Package savedPackage = packageService.savePackageInformation(packageInfo);
		return savedPackage.getPackageId();
	}

	@RequestMapping(value = "/v1/packages/{packageId}/files", method = RequestMethod.POST, consumes = {
			"multipart/form-data" })
	public @ResponseBody FileUploadResponse postFilesToPackage(@PathVariable("packageId") String packageId,
			@RequestParam("qqfile") MultipartFile file, @RequestParam("qqfilename") String filename,
			@RequestParam("qqtotalfilesize") long fileSize,
			@RequestParam(name = "qqtotalparts", defaultValue = "1") int chunks,
			@RequestParam(name = "qqpartindex", defaultValue = "0") int chunk) throws Exception {

		log.info(fileUploadRequest
				.format(new Object[] { "postFilesToPackage", filename, packageId, fileSize, chunks, chunk }));

		packageService.saveFile(file, packageId, filename, shouldAppend(chunk));

		return new FileUploadResponse(true);
	}

	@RequestMapping(value = "/v1/packages/{packageId}/files", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Resource> downloadPackage(@PathVariable String packageId) {
		Resource resource = null;
		try {
			resource = new UrlResource(packageService.getPackageFile(packageId).toUri());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		log.info(fileDownloadRequest.format(new Object[] { packageId, resource.toString() }));

		return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	@RequestMapping(value = "/v1/packages/{packageId}/files/finish", method = RequestMethod.POST)
	public @ResponseBody FileUploadResponse finishUpload(@PathVariable("packageId") String packageId) {
		log.info(finish.format(new Object[] { "finishUpload", packageId }));

		packageService.createZipFile(packageId);

		return new FileUploadResponse(true);
	}

	private boolean shouldAppend(int chunk) {
		return chunk != 0;
	}

}
