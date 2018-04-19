package org.kpmp.upload;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadController {

	@PostMapping("/upload")
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			@RequestParam("packageInformation") PackageInformation packageInformation,
			@RequestParam("fileMetadata") String fileMetadata) {
		String responseString = "Success! You uploaded: ";
		responseString += file.getOriginalFilename() + " (" + file.getSize() + " bytes) ";
		System.out.println(responseString);
		return "{\"message\": \"" + responseString + "\"}";
	}

}
