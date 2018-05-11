package org.kpmp.upload;

import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class PackageInformation {

	private String firstName;
	private String lastName;
	private String packageType;
	private String subjectId;
	private String experimentId;
	private Date experimentDate;
	private String institutionName;
	private String packageTypeOther;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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

	public String getExperimentId() {
		return experimentId;
	}

	public void setExperimentId(String experimentId) {
		this.experimentId = experimentId;
	}

	public Date getExperimentDate() {
		return experimentDate;
	}

	public void setExperimentDate(Date experimentDate) {
		this.experimentDate = experimentDate;
	}

	public String getInstitutionName() {
		return institutionName;
	}

	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}
	
	public String getPackageTypeOther() {
		return packageTypeOther;
	}

	public void setPackageTypeOther(String packageTypeOther) {
		this.packageTypeOther = packageTypeOther;
	}

	@Override
	public String toString() {
		return "firstName: " + firstName + "  lastName: " + lastName + "  packageType: " + packageType + "  subjectId: "
				+ subjectId + "  experimentId: " + experimentId + "  experimentdate: " + experimentDate
				+ "  institutionName: " + institutionName;
	}

}
