package org.kpmp.dao.deprecated;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.kpmp.upload.PackageInformation;

@Entity
@Table(name = "submitter_demographics")
public class SubmitterDemographics {

	@Id
	@GenericGenerator(name = "generator", strategy = "increment")
	@GeneratedValue(generator = "generator")
	@Column(name = "id")
	private int id;
	@Column(name = "first_name")
	private String firstName;
	@Column(name = "last_name")
	private String lastName;
	@Column(name = "created_at")
	private Date createdAt;
	@Column(name = "deleted_at")
	private Date deletedAt;
	@Column(name = "updated_at")
	private Date updatedAt;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "submitter")
	private List<FileSubmission> fileSubmissions;

	public SubmitterDemographics() {
	}

	public SubmitterDemographics(PackageInformation packageInformation, Date createdDate) {
		this.firstName = packageInformation.getFirstName();
		this.lastName = packageInformation.getLastName();
		this.createdAt = createdDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getDeletedAt() {
		return deletedAt;
	}

	public void setDeletedAt(Date deletedAt) {
		this.deletedAt = deletedAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<FileSubmission> getFileSubmissions() {
		return fileSubmissions;
	}

	public void setFileSubmissions(List<FileSubmission> fileSubmissions) {
		this.fileSubmissions = fileSubmissions;
	}

}
