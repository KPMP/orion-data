package org.kpmp.dao;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "file_submissions")
public class FileSubmissions {

	@Id
	@Column(name = "id")
	private int id;
	@Column(name = "file_size")
	private BigInteger fileSize;
	@Column(name = "filename")
	private String filename;
	@Column(name = "created_at")
	private Date createdAt;
	@Column(name = "updated_at")
	private Date updatedAt;
	@Column(name = "deleted_at")
	private Date deletedAt;

	@ManyToOne
	@JoinColumn(name = "file_format_id", referencedColumnName = "id")
	private FileFormats fileFormat;

	@ManyToOne
	@JoinColumn(name = "submitter_demographics_id", referencedColumnName = "id")
	private SubmitterDemographics submitter;

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

	public BigInteger getFileSize() {
		return fileSize;
	}

	public void setFileSize(BigInteger fileSize) {
		this.fileSize = fileSize;
	}

	public FileFormats getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(FileFormats fileFormat) {
		this.fileFormat = fileFormat;
	}

}
