package org.kpmp.view;

import java.util.Date;

import org.kpmp.dao.FileSubmission;
import org.springframework.stereotype.Component;

@Component
public class PackageView {

	private String name;
	private String site;
	private String packageId;
	private String packageType;
	private String subjectId;
	private Date uploadDate;

	public PackageView(FileSubmission fileSubmission) {
		this.name = fileSubmission.getSubmitter().getFirstName() + " " + fileSubmission.getSubmitter().getLastName();
		this.site = fileSubmission.getInstitution().getInstitutionName();
		this.packageId = "need universal Id";
		this.packageType = fileSubmission.getUploadPackage().getPackageType().getPackageType();
		this.subjectId = fileSubmission.getUploadPackage().getSubjectId();
		this.uploadDate = fileSubmission.getUploadPackage().getCreatedAt();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}
}
