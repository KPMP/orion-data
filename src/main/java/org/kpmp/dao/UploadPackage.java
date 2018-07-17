package org.kpmp.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.kpmp.upload.PackageInformation;

@Entity
@Table(name = "upload_package")
public class UploadPackage {

	@Id
	@GenericGenerator(name = "generator", strategy = "increment")
	@GeneratedValue(generator = "generator")
	@Column(name = "id")
	private int id;
	@Column(name = "subject_id")
	private String subjectId;
	@Column(name = "experiment_id")
	private String experimentId;
	@Column(name = "performed_at")
	private Date experimentDate;
	@Column(name = "created_at")
	private Date createdAt;

	@ManyToOne
	@JoinColumn(name = "package_type_id", referencedColumnName = "id")
	private PackageType packageType;

	@OneToOne
	@JoinTable(name = "upload_package_to_package_type_other", joinColumns = {
			@JoinColumn(name = "upload_package_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "package_type_other_id", referencedColumnName = "id") })
	private PackageTypeOther packageTypeOther;

	@ManyToOne
	@JoinColumn(name = "protocol_id", referencedColumnName = "id")
	private Protocol protocol;

	public UploadPackage() {
	}

	public UploadPackage(PackageInformation packageInformation, Date createdDate) {
		this.experimentDate = packageInformation.getExperimentDate();
		this.subjectId = packageInformation.getSubjectId();
		this.experimentId = packageInformation.getExperimentId();
		this.createdAt = createdDate;
	}

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "uploadPackage")
	private List<FileSubmission> fileSubmissions;

	public int getId() {
		return id;
	}

	public void setId(int id) {
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

	public Date getExperimentDate() {
		return experimentDate;
	}

	public void setExperimentDate(Date experimentDate) {
		this.experimentDate = experimentDate;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public List<FileSubmission> getFileSubmissions() {
		return fileSubmissions;
	}

	public void setFileSubmissions(List<FileSubmission> fileSubmissions) {
		this.fileSubmissions = fileSubmissions;
	}

	public PackageType getPackageType() {
		return packageType;
	}

	public void setPackageType(PackageType packageType) {
		this.packageType = packageType;
	}

	public PackageTypeOther getPackageTypeOther() {
		return packageTypeOther;
	}

	public void setPackageTypeOther(PackageTypeOther packageTypeOther) {
		this.packageTypeOther = packageTypeOther;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}
}
