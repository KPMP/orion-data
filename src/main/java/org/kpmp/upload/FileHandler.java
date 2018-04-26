package org.kpmp.upload;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
class FileHandler {

	@Value("${file.base.path}")
	private String basePath;

	public File saveMultipartFile(MultipartFile file, int packageId, String filename, boolean fullFile)
			throws IOException {
		File packageDirectory = new File(basePath + File.separator + "package" + packageId);
		if (!packageDirectory.exists()) {
			packageDirectory.mkdirs();
		}
		File fileToSave = new File(basePath + File.separator + "package" + packageId + File.separator + filename);

		if (!fullFile) {
			FileUtils.writeByteArrayToFile(fileToSave, file.getBytes(), true);
		} else {
			FileUtils.writeByteArrayToFile(fileToSave, file.getBytes());
		}
		return fileToSave;
	}

}
