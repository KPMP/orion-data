package org.kpmp.packages.validation;

import org.springframework.stereotype.Component;

@Component
public class PackageFilesRequest {

	private String packageId;
	private String filenames;

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public String getFilenames() {
		return filenames;
	}

	public void setFilenames(String filenames) {
		this.filenames = filenames;
	}

}
