package org.kpmp.packages;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PackageZipService {

	private static final String METADATA_JSON_FILENAME = "metadata.json";
	private FilePathHelper filePathHelper;

	@Autowired
	public PackageZipService(FilePathHelper filePathHelper) {
		this.filePathHelper = filePathHelper;
	}

	public void createZipFile(String packageMetadataString) throws JSONException, IOException {
		JSONObject packageMetadata = new JSONObject(packageMetadataString);
		JSONArray files = (JSONArray) packageMetadata.get(PackageKeys.FILES.getKey());
		String packageId = packageMetadata.getString(PackageKeys.ID.getKey());
		String packagePath = filePathHelper.getPackagePath(packageId);
		String zipFileName = filePathHelper.getZipFileName(packageId);
		File tempZipFileHandle = new File(zipFileName + ".tmp");
		try (ZipArchiveOutputStream zipFile = new ZipArchiveOutputStream(tempZipFileHandle)) {

			zipFile.setMethod(ZipArchiveOutputStream.DEFLATED);
			zipFile.setEncoding("UTF-8");
			for (int i = 0; i < files.length(); i++) {
				JSONObject fileObject = files.getJSONObject(i);
				String fileName = fileObject.getString(PackageKeys.FILE_NAME.getKey());
				File file = new File(packagePath + fileName);
				ZipArchiveEntry entry = new ZipArchiveEntry(fileName);
				entry.setSize(fileObject.getLong(PackageKeys.SIZE.getKey()));
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
			ZipArchiveEntry metadataEntry = new ZipArchiveEntry(METADATA_JSON_FILENAME);
			metadataEntry.setSize(packageMetadataString.getBytes().length);
			zipFile.putArchiveEntry(metadataEntry);
			zipFile.write(packageMetadataString.getBytes());
			zipFile.closeArchiveEntry();
		}
		File zipFileHandle = new File(zipFileName);
		tempZipFileHandle.renameTo(zipFileHandle);
	}

}
