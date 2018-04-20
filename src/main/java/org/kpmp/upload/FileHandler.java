package org.kpmp.upload;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
class FileHandler {

	@Value("${file.base.path}")
	private String basePath;

	public String saveFile(MultipartFile file, int packageId) throws IllegalStateException, IOException {
		File packageDirectory = new File(basePath + File.separator + "package" + packageId);
		if (!packageDirectory.exists()) {
			packageDirectory.mkdirs();
		}

		File fileToSave = new File(
				basePath + File.separator + "package" + packageId + File.separator + file.getOriginalFilename());
		file.transferTo(fileToSave);
		return fileToSave.getPath();
	}

}
