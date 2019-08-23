package org.kpmp.externalProcess;

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

	public String buildZipCommand(String packageId, String metadataJson) {
		StringBuilder commandString = new StringBuilder("java -jar zipWorker.jar ");

		String packagePath = filePathHelper.getPackagePath(packageId);
		List<String> fullPaths = filePathHelper.getFilenames(packagePath);
		for (String filePath : fullPaths) {
			commandString.append("--zip.fileNames=" + filePath + " ");
		}

		String zipFileName = filePathHelper.getZipFileName(packageId);
		commandString.append("--zip.zipFilePath=" + zipFileName + " ");
		commandString.append("--zip.additionalFileData=\"metadata.json|" + metadataJson + "\"");

		return commandString.toString();
	}
}
