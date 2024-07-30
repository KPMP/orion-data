package org.miktmc.packages;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.miktmc.users.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Document(collection = "packages")
@JsonPropertyOrder({ "packageId", "createdAt", "packageType", "submitter", "tisName", "protocol", "biopsyId",
		"experimentDate", "description", "attachments", "modifications" })
public class Package {

	@Id
	private String packageId;
	private String packageType;
	private Date createdAt;
	private String siteName;
	private String protocol;
	private String biopsyId;
	private Date experimentDate;
	private String description;
    private String study;

	
    @DBRef(lazy = false)
	private User submitter;
	@Nullable
	private Boolean largeFilesChecked = false;

	@Field("files")
	private List<Attachment> attachments = new ArrayList<>();

	@Field("modifications")
	private List<String> modifications = new ArrayList<>();

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

	public List<String> getOriginalFilenames() {
		List<String> fileNames = new ArrayList<>();
		for (Attachment file: this.attachments) {
			fileNames.add(file.getOriginalFileName());
		}
		return fileNames;
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

	public String getBiopsyId() {
		return biopsyId;
	}

	public void setBiopsyId(String biopsyId) {
		this.biopsyId = biopsyId;
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

	public List<String> getModifications() {
		return modifications;
	}

	public void setModifications(List<String> modifications) {
		this.modifications = modifications;
	}

	@Override
	public String toString() {
		return "packageId: " + packageId + ", packageType: " + packageType + ", createdAt: " + createdAt
				+ ", submitterId: " + submitter.getId() + ", protocol: " + protocol + ", biopsyId: " + biopsyId
				+ ", experimentDate: " + experimentDate + ", description: " + description + ", siteName: " + siteName
                + ", study: " + study
				+ ", number of attachments: " + attachments.size()
				+ ", modifications: [" + String.join("; ", modifications) + "]";
	}

}
