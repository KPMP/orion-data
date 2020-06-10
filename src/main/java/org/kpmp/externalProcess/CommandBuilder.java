package org.kpmp.externalProcess;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.kpmp.packages.FilePathHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommandBuilder {

	private FilePathHelper filePathHelper;

	@Autowired
	public CommandBuilder(FilePathHelper filePathHelper) {
		this.filePathHelper = filePathHelper;
	}

	public String[] buildZipCommand(String packageId, String zipWorkerPath) {
		List<String> commandArgs = new ArrayList<>();
		commandArgs.add("java");
		commandArgs.add("-jar");
		commandArgs.add(zipWorkerPath);

		String packagePath = filePathHelper.getPackagePath(packageId);
		List<String> fileNames = filePathHelper.getFilenames(packagePath);
		for (String fileName : fileNames) {
			commandArgs.add("--zip.fileNames=" + packagePath + File.separator + fileName);
		}

		String zipFileName = filePathHelper.getZipFileName(packageId);
		commandArgs.add("--zip.zipFilePath=" + zipFileName);

		return commandArgs.toArray(new String[0]);
	}

	public String[] buildZipCommand(String packageId) {
		return buildZipCommand(packageId, "/home/gradle/zipWorker/zipWorker.jar");
	}
}

