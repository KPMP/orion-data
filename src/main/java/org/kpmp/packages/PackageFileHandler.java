package org.kpmp.packages;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PackageFileHandler {

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

	public File saveFile(String fileContents, String packageId, String filename, boolean shouldOverwrite)
			throws IOException {
		String packageDirectoryPath = filePathHelper.getPackagePath(packageId);
		File packageDirectory = new File(packageDirectoryPath);

		if (!packageDirectory.exists()) {
			Files.createDirectories(Paths.get(packageDirectoryPath));
		}

		File fileToSave = new File(packageDirectoryPath + File.separator + filename);

		if (fileToSave.exists() && shouldOverwrite) {
			fileToSave.delete();
		} else if (fileToSave.exists() && !shouldOverwrite) {
			throw new FileAlreadyExistsException(fileToSave.getPath());
		}

		FileUtils.writeStringToFile(fileToSave, fileContents, "UTF-8");

		return fileToSave;
	}
}
