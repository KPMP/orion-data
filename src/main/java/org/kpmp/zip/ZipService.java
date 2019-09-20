package org.kpmp.zip;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.springframework.stereotype.Component;

@Component
public class ZipService {

	private static final String TMP_FILE_EXTENSION = ".tmp";

	public void createZipFile(String zipFilePath, List<String> filePaths, Map<String, String> additionalFileInformation)
			throws IOException {
		File tempZipFileHandle = new File(zipFilePath + TMP_FILE_EXTENSION);
		try (ZipArchiveOutputStream zipFile = new ZipArchiveOutputStream(new File(zipFilePath))) {
			zipFile.setMethod(ZipArchiveOutputStream.DEFLATED);
			zipFile.setEncoding(StandardCharsets.UTF_8.name());
			for (String filePath : filePaths) {
				File file = new File(filePath);
				ZipArchiveEntry entry = new ZipArchiveEntry(file.getName());
				entry.setSize(file.length());
				zipFile.putArchiveEntry(entry);
				try (FileInputStream fileInputStream = new FileInputStream(file)) {
					try (BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {
						byte[] buffer = new byte[32768];
						int data = 0;
						while ((data = bufferedInputStream.read(buffer, 0, buffer.length)) != -1) {
							zipFile.write(buffer, 0, data);
						}
						zipFile.flush();
						bufferedInputStream.close();
					}
					fileInputStream.close();
				}
				zipFile.closeArchiveEntry();
			}

			Set<String> keys = additionalFileInformation.keySet();
			for (String additionalFileName : keys) {
				ZipArchiveEntry additionalEntry = new ZipArchiveEntry(additionalFileName);
				String additionaFileContents = additionalFileInformation.get(additionalFileName);
				additionalEntry.setSize(additionaFileContents.getBytes().length);
				zipFile.putArchiveEntry(additionalEntry);
				zipFile.write(additionaFileContents.getBytes(StandardCharsets.UTF_8));
				zipFile.closeArchiveEntry();
			}

			File zipFileHandle = new File(zipFilePath);
			tempZipFileHandle.renameTo(zipFileHandle);
		}
	}

}
