package org.kpmp.packages;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PackageFileHandler {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private FilePathHelper filePathHelper;

	@Autowired
	public PackageFileHandler(FilePathHelper filePathHelper) {
		this.filePathHelper = filePathHelper;
	}

	public void saveMultipartFile(MultipartFile file, String packageId, String filename, boolean shouldAppend)
			throws IOException {
		String packageDirectoryPath = filePathHelper.getPackagePath(packageId);
		File packageDirectory = new File(packageDirectoryPath);
		if (!packageDirectory.exists()) {
			Files.createDirectories(Paths.get(packageDirectoryPath));
		}

		File fileToSave = new File(packageDirectoryPath + File.separator + filename);
		if (!shouldAppend && fileToSave.exists()) {
			throw new FileAlreadyExistsException(fileToSave.getPath());
		} else {
			InputStream inputStream = file.getInputStream();
			FileOutputStream fileOutputStream = new FileOutputStream(fileToSave, shouldAppend);

			IOUtils.copy(inputStream, fileOutputStream);
		}
	}

	public void assertPackageFileHasSize(String packageId, String filename, long expectedSize) throws RuntimeException {
		String packageDirectoryPath = filePathHelper.getPackagePath(packageId);
		File savedFile = new File(packageDirectoryPath + File.separator + filename);
		long actualSize = savedFile.length();
		String msg = String.format("Request|assertPackageFileHasSize|%s|%s|%d|%d", packageId, filename, expectedSize, actualSize);
		log.info(msg);

		if(actualSize != expectedSize) {
			throw new RuntimeException(msg);
		}
	}

}
