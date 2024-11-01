package org.miktmc.packages;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FilePathHelper {

	@Value("${file.base.path}")
	private String basePath;

	public String getPackagePath(String packageId, String study) {
		String studyFolder = study.replaceAll(" ", "");
		return basePath + File.separator + studyFolder + File.separator + "package_" + packageId + File.separator;
	}

	public String getFilePath(String packageId, String study, String fileName) {
		return getPackagePath(packageId, study) + fileName;
	}

	public List<String> getFilenames(String path) {
		List<String> filenames = new ArrayList<>();
		File packageDir = new File(path);
		File[] listOfFiles = packageDir.listFiles();
		for (File file : listOfFiles) {
            if (!(file.getName().toLowerCase().startsWith(".nfs"))){
                filenames.add(file.getName());
            }
		}
		return filenames;
	}
}