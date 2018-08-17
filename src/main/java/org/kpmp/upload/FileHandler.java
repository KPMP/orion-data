package org.kpmp.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
class FileHandler {

	@Value("${file.base.path}")
	private String basePath;

	private FilePathHelper filePathHelper;

	public FileHandler(FilePathHelper filePathHelper) {
		this.filePathHelper = filePathHelper;
	}

	public File saveMultipartFile(MultipartFile file, int packageId, String filename, boolean shouldAppend)
			throws IOException {
		File packageDirectory = new File(basePath + File.separator + "package" + packageId);
		if (!packageDirectory.exists()) {
			packageDirectory.mkdirs();
		}
		if (filename.equalsIgnoreCase(filePathHelper.getMetadataFileName())) {
			filename = filename.replace(".", "_user.");
		}
		File fileToSave = new File(basePath + File.separator + "package" + packageId + File.separator + filename);
		InputStream inputStream = file.getInputStream();
		FileOutputStream fileOutputStream;

		if (shouldAppend) {
			fileOutputStream = new FileOutputStream(fileToSave, true);
		} else {
			fileOutputStream = new FileOutputStream(fileToSave);
		}
		IOUtils.copy(inputStream, fileOutputStream);
		return fileToSave;
	}

}
