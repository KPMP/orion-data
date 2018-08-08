package org.kpmp.view;

import java.util.Date;

import org.kpmp.dao.FileSubmission;
import org.springframework.stereotype.Component;

@Component
public class PackageView {

	private String researcher;
	private String institution;
	private String packageId;
	private String packageType;
	private String subjectId;
	private Date createdAt;
	private int databaseId;

	public PackageView(FileSubmission fileSubmission) {
		this.researcher = fileSubmission.getSubmitter().getFirstName() + " "
				+ fileSubmission.getSubmitter().getLastName();
		this.institution = fileSubmission.getInstitution().getInstitutionName();
		this.packageId = fileSubmission.getUploadPackage().getUniversalId();
		this.packageType = fileSubmission.getUploadPackage().getPackageType().getPackageType();
		this.subjectId = fileSubmission.getUploadPackage().getSubjectId();
		this.createdAt = fileSubmission.getUploadPackage().getCreatedAt();
		this.databaseId = fileSubmission.getUploadPackage().getId();
	}

	public PackageView() {
	}

	public String getResearcher() {
		return researcher;
	}

	public void setResearcher(String researcher) {
		this.researcher = researcher;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
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

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date uploadDate) {
		this.createdAt = uploadDate;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public int getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(int databaseId) {
		this.databaseId = databaseId;
	}
}
