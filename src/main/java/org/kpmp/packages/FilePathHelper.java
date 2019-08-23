package org.kpmp.packages;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FilePathHelper {

	@Value("${file.base.path}")
	private String basePath;

	public String getPackagePath(String packageId) {
		return basePath + File.separator + "package_" + packageId + File.separator;
	}

	public String getZipFileName(String packageId) {
		return getPackagePath(packageId) + packageId + ".zip";
	}

	public List<String> getFilenames(String path) {
		List<String> filenames = new ArrayList<>();
		File packageDir = new File(path);
		File[] listOfFiles = packageDir.listFiles();
		for (File file : listOfFiles) {
			filenames.add(file.getName());
		}
		return filenames;
	}
}