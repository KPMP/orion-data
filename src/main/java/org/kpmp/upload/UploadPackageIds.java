package org.kpmp.upload;

import org.springframework.stereotype.Component;

@Component
public class UploadPackageIds {

	private int packageId;
	private int submitterId;
	private int institutionId;

	public UploadPackageIds(int packageId, int submitterId, int institutionId) {
		this.packageId = packageId;
		this.submitterId = submitterId;
		this.institutionId = institutionId;
	}

	public UploadPackageIds() {
	}

	public int getPackageId() {
		return packageId;
	}

	public void setPackageId(int packageId) {
		this.packageId = packageId;
	}

	public int getSubmitterId() {
		return submitterId;
	}

	public void setSubmitterId(int submitterId) {
		this.submitterId = submitterId;
	}

	public int getInstitutionId() {
		return institutionId;
	}

	public void setInstitutionId(int institutionId) {
		this.institutionId = institutionId;
	}

}
