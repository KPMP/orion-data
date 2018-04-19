package org.kpmp.dao;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "file_submissions")
public class FileSubmissions {

	@Id
	@GenericGenerator(name = "generator", strategy = "increment")
	@GeneratedValue(generator = "generator")
	@Column(name = "id")
	private int id;
	@Column(name = "file_size")
	private Long fileSize;
	@Column(name = "filename")
	private String filename;
	@Column(name = "created_at")
	private Date createdAt;
	@Column(name = "updated_at")
	private Date updatedAt;
	@Column(name = "deleted_at")
	private Date deletedAt;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "file_meta_entries_id", referencedColumnName = "id", insertable = false, updatable = false)
	private FileMetadataEntries fileMetadata;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "file_format_id", referencedColumnName = "id")
	private FileFormats fileFormat;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "submitter_demographics_id", referencedColumnName = "id")
	private SubmitterDemographics submitter;

	@ManyToOne
	@JoinColumn(name = "case_demographics_id", referencedColumnName = "id")
	private UploadPackage uploadPackage;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "institution_demographics_id", referencedColumnName = "id")
	private InstitutionDemographics institution;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Date getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(Date deletedAt) {
		this.deletedAt = deletedAt;
	}

	public SubmitterDemographics getSubmitter() {
		return submitter;
	}

	public void setSubmitter(SubmitterDemographics submitter) {
		this.submitter = submitter;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public FileFormats getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(FileFormats fileFormat) {
		this.fileFormat = fileFormat;
	}

	public UploadPackage getUploadPackage() {
		return uploadPackage;
	}

	public void setUploadPackage(UploadPackage uploadPackage) {
		this.uploadPackage = uploadPackage;
	}

	public FileMetadataEntries getFileMetadata() {
		return fileMetadata;
	}

	public void setFileMetadata(FileMetadataEntries fileMetadata) {
		this.fileMetadata = fileMetadata;
	}

	public InstitutionDemographics getInstitution() {
		return institution;
	}

	public void setInstitution(InstitutionDemographics institution) {
		this.institution = institution;
	}

}
