package org.kpmp.packages.state;

import java.util.Date;

public class PackageNotificationInfo {

	private String packageId;
	private String packageType;
	private Date datePackageSubmitted;
	private String submitter;
	private String specimenId;
	private String origin;

	public PackageNotificationInfo() {
	}

	public PackageNotificationInfo(String packageId, String packageType, Date datePackageSubmitted,
			String submitterName, String specimenId, String origin) {
		this.packageId = packageId;
		this.packageType = packageType;
		this.datePackageSubmitted = datePackageSubmitted;
		submitter = submitterName;
		this.specimenId = specimenId;
		this.setOrigin(origin);
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

	public Date getDatePackageSubmitted() {
		return datePackageSubmitted;
	}

	public void setDatePackageSubmitted(Date dateSubmitted) {
		this.datePackageSubmitted = dateSubmitted;
	}

	public String getSubmitter() {
		return submitter;
	}

	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}

	public String getSpecimenId() {
		return specimenId;
	}

	public void setSpecimenId(String specimenId) {
		this.specimenId = specimenId;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

}
