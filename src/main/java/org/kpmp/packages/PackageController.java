package org.kpmp.packages;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	private static final MessageFormat fileUploadRequest = new MessageFormat("Request|{0}|{1}|{2}|{3}|{4}|{5}");

	@Autowired
	public PackageController(PackageService packageService) {
		this.packageService = packageService;
	}

	@RequestMapping(value = "/v1/packages", method = RequestMethod.GET)
	public @ResponseBody List<Package> getAllPackages() {
		return packageService.findAllPackages();
	}

	@RequestMapping(value = "/v1/packages", method = RequestMethod.POST)
	public @ResponseBody String postPackageInfo(@RequestBody Package packageInfo) {
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
			@RequestParam(name = "qqpartindex", defaultValue = "0") int chunk) throws IOException {

		log.info(fileUploadRequest
				.format(new Object[] { "postFilesToPackage", filename, packageId, fileSize, chunks, chunk }));

		packageService.saveFile(file, packageId, filename, fileSize, isInitialChunk(chunk));
		// determine if I should start new file, or append

		// save the file
		return new FileUploadResponse(true);
	}

	private boolean isInitialChunk(int chunk) {
		return chunk == 0;
	}
}
