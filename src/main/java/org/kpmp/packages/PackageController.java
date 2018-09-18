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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PackageController {

	private PackageService packageService;
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static final MessageFormat fileDownloadRequest = new MessageFormat("Request|{0}|{1}");

	@Autowired
	public PackageController(PackageService packageService) {
		this.packageService = packageService;

	}

	@RequestMapping(value = "/v1/packages", method = RequestMethod.GET)
	public @ResponseBody List<Package> getAllPackages() {
		return packageService.findAllPackages();
	}

	@RequestMapping(value = "/v1/packages/{packageId}/files", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Resource> downloadPackage(@PathVariable String packageId) {
		Resource resource = null;
		try {
			resource = new UrlResource(packageService.getPackageFile(packageId).toUri());
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		log.info(fileDownloadRequest.format(new Object[]{packageId, resource.toString()}));
		return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);
	}

}
