package org.kpmp.upload;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {

	@RequestMapping(value = "/upload/packageInfo", consumes = { "application/json" }, method = RequestMethod.POST)
	public int uploadPackageInfo(@RequestBody PackageInformation packageInformation) {
		System.err.println(packageInformation.getFirstName());
		return -1;
	}

	@RequestMapping(value = "/upload", consumes = { "multipart/form-data" }, method = RequestMethod.POST)
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			@RequestParam("fileMetadata") String fileMetadata, @RequestParam("packageId") int packageId) {
		String responseString = "Success! You uploaded: ";
		responseString += file.getOriginalFilename() + " (" + file.getSize() + " bytes) ";
		System.out.println(responseString);
		System.out.println(fileMetadata);
		System.out.println(packageId);
		return "{\"message\": \"" + responseString + "\"}";
	}

}
