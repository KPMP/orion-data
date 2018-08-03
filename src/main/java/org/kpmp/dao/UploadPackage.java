package org.kpmp.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
	@Column(name = "performed_at")
	private Date experimentDate;
	@Column(name = "created_at")
	private Date createdAt;
	@Column(name = "uuid")
	private String universalId;
	@ManyToOne
	@JoinColumn(name = "protocol_id", referencedColumnName = "id")
	private Protocol protocol;

	@ManyToOne
	@JoinColumn(name = "package_type_id", referencedColumnName = "id")
	@JsonIgnoreProperties("uploadPackages")
	private PackageType packageType;

	@OneToOne
	@JoinTable(name = "upload_package_to_package_type_other", joinColumns = {
			@JoinColumn(name = "upload_package_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "package_type_other_id", referencedColumnName = "id") })
	private PackageTypeOther packageTypeOther;

	public UploadPackage() {
	}

	public UploadPackage(PackageInformation packageInformation, Date createdDate) {
		this.experimentDate = packageInformation.getExperimentDate();
		this.subjectId = packageInformation.getSubjectId();
		this.createdAt = createdDate;
	}

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "uploadPackage", fetch = FetchType.EAGER)
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

	public String getUniversalId() {
		return universalId;
	}

	public void setUniversalId(String universalId) {
		this.universalId = universalId;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}
}
