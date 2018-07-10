package org.kpmp.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UploadPackageMetadata {

	private String id;
	private String subjectId;
	private String experimentId;
	private String experimentDate;
	private String createdAt;
	private String packageType;
	private String submitterFirstName;
	private String submitterLastName;
	private String institution;
	private List<FileSubmissionJSON> files;

	private static final class FileSubmissionJSON {
		private final String path;
		private final long size;
		private final String fileName;
		private final String description;
		private final String universalId;

		private FileSubmissionJSON(FileSubmission fileSubmission) {
			this.path = fileSubmission.getFilePath();
			this.size = fileSubmission.getFileSize();
			this.description = fileSubmission.getFileMetadata().getMetadata();
			this.fileName = fileSubmission.getFilename();
			this.universalId = fileSubmission.getUniversalId();
		}

		@SuppressWarnings("unused")
		public long getSize() {
			return size;
		}

		@SuppressWarnings("unused")
		public String getFileName() {
			return fileName;
		}

		@SuppressWarnings("unused")
		public String getDescription() {
			return description;
		}

		@SuppressWarnings("unused")
		public String getPath() {
			return path;
		}

		@SuppressWarnings("unused")
		public String getUniversalId() {
			return universalId;
		}
	}

	public UploadPackageMetadata(UploadPackage uploadPackage) {
		this.id = uploadPackage.getUniversalId();
		this.subjectId = uploadPackage.getSubjectId();
		this.experimentId = uploadPackage.getExperimentId();
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		if (uploadPackage.getExperimentDate() != null) {
			this.experimentDate = dt.format(uploadPackage.getExperimentDate());
		}
		this.createdAt = dt.format(uploadPackage.getCreatedAt());
		if (uploadPackage.getPackageTypeOther() != null) {
			this.packageType = uploadPackage.getPackageTypeOther().getPackageType();
		} else {
			this.packageType = uploadPackage.getPackageType().getPackageType();
		}
		this.files = new ArrayList<>();
		List<FileSubmission> fileSubmissionList = uploadPackage.getFileSubmissions();
		SubmitterDemographics submitter = new SubmitterDemographics();
		InstitutionDemographics demographics = new InstitutionDemographics();

		for(Iterator<FileSubmission> it = fileSubmissionList.iterator(); it.hasNext();) {
			FileSubmission fileSubmission = it.next();
			this.files.add(new FileSubmissionJSON(fileSubmission));
			submitter = fileSubmission.getSubmitter();
			demographics = fileSubmission.getInstitution();
		}

		this.submitterFirstName = submitter.getFirstName();
		this.submitterLastName = submitter.getLastName();
		this.institution = demographics.getInstitutionName();
	}

	public String generateJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getExperimentDate() {
		return experimentDate;
	}

	public void setExperimentDate(String experimentDate) {
		this.experimentDate = experimentDate;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	public String getSubmitterFirstName() {
		return submitterFirstName;
	}

	public void setSubmitterFirstName(String submitterFirstName) {
		this.submitterFirstName = submitterFirstName;
	}

	public String getSubmitterLastName() {
		return submitterLastName;
	}

	public void setSubmitterLastName(String submitterLastName) {
		this.submitterLastName = submitterLastName;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public List<FileSubmissionJSON> getFiles() {
		return files;
	}

	public void setFiles(List<FileSubmissionJSON> files) {
		this.files = files;
	}

}
