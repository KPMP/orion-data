package org.kpmp.view;

import java.util.Date;

public class UploadPackageInfo {

	private String packageId;
	private String packageType;
	private Date submitted;
	private String submitter;
	private String institution;

	public UploadPackageInfo(String packageId, String packageType, Date submitted, String submitter,
			String institution) {
		this.packageId = packageId;
		this.packageType = packageType;
		this.submitted = submitted;
		this.submitter = submitter;
		this.institution = institution;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	public Date getSubmitted() {
		return submitted;
	}

	public void setSubmitted(Date submitted) {
		this.submitted = submitted;
	}

	public String getSubmitter() {
		return submitter;
	}

	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

}
