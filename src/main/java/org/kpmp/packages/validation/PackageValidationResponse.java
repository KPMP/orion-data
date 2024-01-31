package org.kpmp.packages.validation;

import java.util.ArrayList;
import java.util.List;

public class PackageValidationResponse {

	private List<String> metadataFilesNotFoundInGlobus;
	private List<String> globusFilesNotFoundInMetadata;
	private String packageId;
	private List<String> filesFromMetadata;
	private List<String> filesInGlobus;
	private Boolean directoryExists;
	private List<String> directoriesInGlobus;
	
	public List<String> getDirectoriesInGlobus() {
		return directoriesInGlobus;
	}

	public void setDirectoriesInGlobus(List<String> directoriesInGlobus) {
		this.directoriesInGlobus = directoriesInGlobus;
	}

	public List<String> getMetadataFilesNotFoundInGlobus() {
		return metadataFilesNotFoundInGlobus;
	}

	public void addMetadataFileNotFoundInGlobus(String fileNotFound) {
		if (metadataFilesNotFoundInGlobus == null) {
			metadataFilesNotFoundInGlobus = new ArrayList<String>();
		}
		metadataFilesNotFoundInGlobus.add(fileNotFound);
	}

	public List<String> getGlobusFilesNotFoundInMetadata() {
		return globusFilesNotFoundInMetadata;
	}

	public void addGlobusFileNotFoundInMetadata(String fileNotFound) {
		if (globusFilesNotFoundInMetadata == null) {
			globusFilesNotFoundInMetadata = new ArrayList<>();
		}
		globusFilesNotFoundInMetadata.add(fileNotFound);
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public List<String> getFilesFromMetadata() {
		return filesFromMetadata;
	}

	public void setFilesFromMetadata(List<String> filesFromMetadata) {
		this.filesFromMetadata = filesFromMetadata;
	}

	public List<String> getFilesInGlobus() {
		return filesInGlobus;
	}

	public void setFilesInGlobus(List<String> filesInGlobus) {
		this.filesInGlobus = filesInGlobus;
	}

	public Boolean getDirectoryExists() {
		return directoryExists;
	}

	public void setDirectoryExists(Boolean directoryExists) {
		this.directoryExists = directoryExists;
	}

}
