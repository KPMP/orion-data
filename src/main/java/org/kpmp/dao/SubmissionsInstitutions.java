package org.kpmp.dao;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "submissions_institutions")
public class SubmissionsInstitutions {

	@Id
	@Column(name = "id")
	private int id;
	@Column(name = "submission_id")
	private int fileSubmissionId;
	@Column(name = "created_at")
	private Date createdAt;

	@ManyToOne
	@JoinColumn(name = "institution_id", referencedColumnName = "id")
	private InstitutionDemographics institution;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFileSubmissionId() {
		return fileSubmissionId;
	}

	public void setFileSubmissionId(int fileSubmissionId) {
		this.fileSubmissionId = fileSubmissionId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public InstitutionDemographics getInstitution() {
		return institution;
	}

	public void setInstitution(InstitutionDemographics institution) {
		this.institution = institution;
	}
}
