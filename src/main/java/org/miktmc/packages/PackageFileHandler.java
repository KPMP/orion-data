package org.miktmc.packages;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.miktmc.logging.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PackageFileHandler {

	private FilePathHelper filePathHelper;
	private LoggingService logger;

	@Autowired
	public PackageFileHandler(FilePathHelper filePathHelper, LoggingService logger) {
		this.filePathHelper = filePathHelper;
		this.logger = logger;
	}

	public void saveMultipartFile(MultipartFile file, String packageId, String filename, String study, boolean shouldAppend)
			throws IOException {
		String packageDirectoryPath = filePathHelper.getPackagePath(packageId, study);
		File packageDirectory = new File(packageDirectoryPath);
		if (!packageDirectory.exists()) {
			Files.createDirectories(Paths.get(packageDirectoryPath));
		}

		String filePath = packageDirectoryPath + File.separator + filename;
		File fileToSave = new File(filePath);
		if (!shouldAppend && fileToSave.exists()) {
			logger.logErrorMessage(PackageFileHandler.class, packageId, "File " + filePath + " already exists.");
			throw new FileAlreadyExistsException(fileToSave.getPath());
		} else {
			InputStream inputStream = file.getInputStream();
			FileOutputStream fileOutputStream = new FileOutputStream(fileToSave, shouldAppend);

			IOUtils.copy(inputStream, fileOutputStream);
		}
	}
}
