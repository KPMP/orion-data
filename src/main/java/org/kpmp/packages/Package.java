package org.kpmp.packages;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.kpmp.users.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Document(collection = "packages")
@JsonPropertyOrder({ "packageId", "createdAt", "packageType", "submitter", "tisName", "protocol", "subjectId",
		"experimentDate", "description", "attachments" })
public class Package {

	@Id
	private String packageId;
	private String packageType;
	private Date createdAt;
	private String siteName;
	private String protocol;
	private String subjectId;
	private Date experimentDate;
	private String description;
    private String study;
    private String studyFolderName;
	@DBRef(lazy = false)
	private User submitter;
	@Nullable
	private Boolean largeFilesChecked = false;

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

    public String getStudy(){
        return study;
    }

    public void setStudy(String study){
        this.study = study;
    }

    public String getStudyFolderName(){
        if (Objects.equals(study, "CureGN Diabetes")){
            study = "CureGNDiabetes";
            return study;
        }
        return getStudy();
    }

    public void setStudyFolderName(String studyFolderName) {
        this.studyFolderName = studyFolderName;
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

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	@Nullable
	public Boolean getLargeFilesChecked() {
		return largeFilesChecked;
	}

	public void setLargeFilesChecked(@Nullable Boolean largeFilesChecked) {
		this.largeFilesChecked = largeFilesChecked;
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

	@Override
	public String toString() {
		return "packageId: " + packageId + ", packageType: " + packageType + ", createdAt: " + createdAt
				+ ", submitterId: " + submitter.getId() + ", protocol: " + protocol + ", subjectId: " + subjectId
				+ ", experimentDate: " + experimentDate + ", description: " + description + ", siteName: " + siteName
                + ", study: " + study
				+ ", number of attachments: " + attachments.size();
	}

}
