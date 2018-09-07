package org.kpmp.packages;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "packages")
public class Package {

	@Id
	private String packageId;
	private String packageType;
	private Date createdAt;
	private String submitterFirstName;
	private String submitterLastName;
	private String institution;
	@Field("files")
	private List<Attachment> attachments;

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

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date submitted) {
		this.createdAt = submitted;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public String getSubmitterLastName() {
		return submitterLastName;
	}

	public void setSubmitterLastName(String submitterLastName) {
		this.submitterLastName = submitterLastName;
	}

	public String getSubmitterFirstName() {
		return submitterFirstName;
	}

	public void setSubmitterFirstName(String submitterFirstName) {
		this.submitterFirstName = submitterFirstName;
	}

}
