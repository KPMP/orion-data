package org.kpmp.packages;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kpmp.users.User;
import org.kpmp.users.UserJsonMixin;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Document(collection = "packages")
@JsonPropertyOrder({ "packageId", "createdAt", "packageType", "submitter", "institution", "protocol", "subjectId",
		"experimentDate", "description", "attachments" })
public class Package {

	@Id
	private String packageId;
	private String packageType;
	private Date createdAt;
	private String institution;
	private String protocol;
	private String subjectId;
	private Date experimentDate;
	private String description;
	@DBRef
	private User submitter;
	private Boolean regenerateZip;

	@Field("files")
	private List<Attachment> attachments = new ArrayList<>();

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

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	@Nullable
	public Date getExperimentDate() {
		return experimentDate;
	}

	public void setExperimentDate(Date experimentDate) {
		this.experimentDate = experimentDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getSubmitter() {
		return submitter;
	}

	public void setSubmitter(User submitter) {
		this.submitter = submitter;
	}

	public Boolean getRegenerateZip() {
		return regenerateZip;
	}

	public void setRegenerateZip(Boolean regenerateZip) {
		this.regenerateZip = regenerateZip;
	}

	@Override
	public String toString() {
		return "packageId: " + packageId + ", packageType: " + packageType + ", createdAt: " + createdAt
				+ ", submitterId: " + submitter.getId() + ", protocol: " + protocol + ", subjectId: " + subjectId
				+ ", experimentDate: " + experimentDate + ", description: " + description + ", institution: "
				+ institution + ", number of attachments: " + attachments.size() + ", regenerateZip: " + regenerateZip;
	}

	public String generateJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.addMixIn(Package.class, MetadataJsonMixin.class);
		mapper.addMixIn(User.class, UserJsonMixin.class);
		return mapper.writeValueAsString(this);
	}

}
