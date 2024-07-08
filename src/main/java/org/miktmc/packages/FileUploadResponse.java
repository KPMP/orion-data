package org.miktmc.packages;

public class FileUploadResponse {

	private boolean success;

	public FileUploadResponse(boolean success) {
		this.success = success;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}
