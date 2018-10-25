package org.kpmp.packages;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FilePathHelper {

	@Value("${file.base.path}")
	private String basePath;

	public String getPackagePath(String prefix, String suffix) {
		return basePath + File.separator + prefix + File.separator + "package_" + suffix + File.separator;
	}

	public String getPackagePath(String suffix) {
		return basePath + File.separator + "package_" + suffix + File.separator;
	}

	public String getZipFileName(String packageId) {
		return getPackagePath(packageId) + packageId + ".zip";
	}

}