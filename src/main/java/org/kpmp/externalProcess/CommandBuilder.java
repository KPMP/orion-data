package org.kpmp.externalProcess;

import java.util.ArrayList;
import java.util.List;

import org.kpmp.logging.LoggingService;
import org.kpmp.packages.FilePathHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommandBuilder {

	private FilePathHelper filePathHelper;
	private LoggingService logger;

	@Autowired
	public CommandBuilder(FilePathHelper filePathHelper, LoggingService logger) {
		this.filePathHelper = filePathHelper;
		this.logger = logger;
	}

	public String[] buildZipCommand(String packageId, String metadataJson) {
		List<String> commandArgs = new ArrayList<>();
		commandArgs.add("java");
		commandArgs.add("-jar");
		commandArgs.add("/home/gradle/zipWorker/zipWorker.jar");

		String packagePath = filePathHelper.getPackagePath(packageId);
		logger.logInfoMessage(this.getClass(), null, packageId, "CommandBuilder.buildZipCommand",
				"package path is: " + packagePath);
		List<String> fullPaths = filePathHelper.getFilenames(packagePath);
		for (String filePath : fullPaths) {

			logger.logInfoMessage(this.getClass(), null, packageId, "CommandBuilder.buildZipCommand",
					"file path is: " + filePath);
			commandArgs.add("--zip.fileNames=" + filePath);
		}

		String zipFileName = filePathHelper.getZipFileName(packageId);
		commandArgs.add("--zip.zipFilePath=" + zipFileName);
		commandArgs.add("--zip.additionalFileData=\"metadata.json|" + metadataJson + "\"");

		return commandArgs.toArray(new String[0]);
	}
}
