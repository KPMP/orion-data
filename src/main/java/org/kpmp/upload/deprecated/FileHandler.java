package org.kpmp.upload.deprecated;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.kpmp.packages.FilePathHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
class FileHandler {

	FilePathHelper filePathHelper;

	@Autowired
	public FileHandler(FilePathHelper filePathHelper) {
		this.filePathHelper = filePathHelper;
	}

	public File saveMultipartFile(MultipartFile file, String packageUid, String filename, boolean shouldAppend)
			throws IOException {
		String filePath = filePathHelper.getPackagePath("", packageUid);
		File packageDirectory = new File(filePath);
		if (!packageDirectory.exists()) {
			packageDirectory.mkdirs();
		}
		File fileToSave = new File(filePath + filename);

		if (shouldAppend) {
			FileUtils.writeByteArrayToFile(fileToSave, file.getBytes(), true);
		} else {
			FileUtils.writeByteArrayToFile(fileToSave, file.getBytes());
		}
		return fileToSave;
	}

}
