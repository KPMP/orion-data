package org.kpmp.packages;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PackageZipService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static final String METADATA_JSON_FILENAME = "metadata.json";
	private static final String PACKAGE_ID = "packageId";
	private static final String SUBMITTER_ID = "id";
	private FilePathHelper filePathHelper;

	@Autowired
	public PackageZipService(FilePathHelper filePathHelper) {
		this.filePathHelper = filePathHelper;
	}

	public void createZipFile(String packageMetadataString) throws RuntimeException, JSONException, IOException {
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
				Long expectedFilesize = fileObject.getLong(PackageKeys.SIZE.getKey());
				assertPackageFileHasSize(packageId, fileName, expectedFilesize);
				File file = new File(packagePath + fileName);
				ZipArchiveEntry entry = new ZipArchiveEntry(fileName);
				entry.setSize(fileObject.getLong(PackageKeys.SIZE.getKey()));
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
			packageMetadata = cleanUpPackageObject(packageMetadata);
			ZipArchiveEntry metadataEntry = new ZipArchiveEntry(METADATA_JSON_FILENAME);
			String metadataToSave = formatPackageMetadata(packageMetadata.toString());
			metadataEntry.setSize(metadataToSave.getBytes().length);
			zipFile.putArchiveEntry(metadataEntry);
			zipFile.write(metadataToSave.getBytes(StandardCharsets.UTF_8));
			zipFile.closeArchiveEntry();
		}
		File zipFileHandle = new File(zipFileName);
		tempZipFileHandle.renameTo(zipFileHandle);
	}

	public String formatPackageMetadata(String packageMetadataString) {
		return packageMetadataString.replaceAll("\\\\/", "/");
	}

	private JSONObject cleanUpPackageObject(JSONObject json) throws JSONException {
		json.remove(PackageKeys.REGENERATE_ZIP.getKey());
		json.remove(PackageKeys.CLASS.getKey());
		json.remove(PackageKeys.VERSION.getKey());
		json.put(PACKAGE_ID, json.get(PackageKeys.ID.getKey()));
		json.remove(PackageKeys.ID.getKey());
		JSONObject submitterObject = json.getJSONObject(PackageKeys.SUBMITTER.getKey());
		submitterObject.remove(SUBMITTER_ID);
		json.put(PackageKeys.SUBMITTER.getKey(), submitterObject);
		return json;
	}

	public void assertPackageFileHasSize(String packageId, String filename, long expectedSize) throws RuntimeException {
		String packageDirectoryPath = filePathHelper.getPackagePath(packageId);
		File savedFile = new File(packageDirectoryPath + File.separator + filename);
		long actualSize = savedFile.length();
		String msg = String.format("Request|assertPackageFileHasSize|%s|%s|%d|%d", packageId, filename, expectedSize, actualSize);
		log.info(msg);

		if(actualSize != expectedSize) {
			throw new RuntimeException(msg);
		}
	}

}
