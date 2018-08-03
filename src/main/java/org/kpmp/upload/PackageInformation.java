package org.kpmp.upload;

import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class PackageInformation {

	private String firstName;
	private String lastName;
	private String packageType;
	private String subjectId;
	private Date experimentDate;
	private String institutionName;
	private String packageTypeOther;
	private String protocol;

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
		return "firstName: " + firstName + "  lastName: " + lastName + "  packageType: " + packageType + "   protocol: "
				+ protocol + "  subjectId: " + subjectId + "  experimentdate: " + experimentDate + "  institutionName: "
				+ institutionName + "  package type other: " + packageTypeOther;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

}
