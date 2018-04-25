package org.kpmp.view;

import java.util.Date;

import org.kpmp.dao.FileSubmission;
import org.springframework.stereotype.Component;

@Component
public class FileUpload {

	private String researcher;
	private String institution;
	private String packageType;
	private Date experimentDate;
	private String subjectId;
	private String experimentId;

	public FileUpload(FileSubmission fileSubmission) {
		this.researcher = fileSubmission.getSubmitter().getFirstName() + " "
				+ fileSubmission.getSubmitter().getLastName();
		this.institution = fileSubmission.getInstitution().getInstitutionName();
		this.packageType = fileSubmission.getUploadPackage().getPackageType().getPackageType();
		this.experimentDate = fileSubmission.getUploadPackage().getExperimentDate();
	}

	public FileUpload() {

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

	public Date getExperimentDate() {
		return experimentDate;
	}

	public void setExperimentDate(Date experimentDate) {
		this.experimentDate = experimentDate;
	}

	public String getExperimentId() {
		return experimentId;
	}

	public void setExperimentId(String experimentId) {
		this.experimentId = experimentId;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

}
