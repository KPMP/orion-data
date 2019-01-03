package org.kpmp.packages;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PackageZipService {

	private FilePathHelper filePathHelper;

	@Autowired
	public PackageZipService(FilePathHelper filePathHelper) {
		this.filePathHelper = filePathHelper;
	}

	public void createZipFile(Package packageInformation) throws IOException {
		List<Attachment> attachments = packageInformation.getAttachments();
		String packagePath = filePathHelper.getPackagePath(packageInformation.getPackageId());
		String zipFileName = filePathHelper.getZipFileName(packageInformation.getPackageId());
		File tempZipFileHandle = new File(zipFileName + ".tmp");
		try (ZipArchiveOutputStream zipFile = new ZipArchiveOutputStream(tempZipFileHandle)) {

			zipFile.setMethod(ZipArchiveOutputStream.DEFLATED);
			zipFile.setEncoding("UTF-8");
			for (Attachment attachment : attachments) {
				File file = new File(packagePath + attachment.getFileName());
				ZipArchiveEntry entry = new ZipArchiveEntry(attachment.getFileName());
				entry.setSize(attachment.getSize());
				zipFile.putArchiveEntry(entry);
				try (FileInputStream fileInputStream = new FileInputStream(file)) {
					try (BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {
						byte[] buffer = new byte[32768];
						int data = bufferedInputStream.read(buffer);
						while (data != -1) {
							zipFile.write(buffer);
							data = bufferedInputStream.read(buffer);
						}
					}
				}
				zipFile.closeArchiveEntry();
			}
		}
		File zipFileHandle = new File(zipFileName);
		tempZipFileHandle.renameTo(zipFileHandle);

	}

}
