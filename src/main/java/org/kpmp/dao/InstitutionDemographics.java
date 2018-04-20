package org.kpmp.dao;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "institution_demographics")
public class InstitutionDemographics {

	@Id
	@Column(name = "id")
	private int id;
	@Column(name = "inst_name")
	private String institutionName;
	@Column(name = "inst_shortname")
	private String institutionShortName;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "institution")
	private List<FileSubmissions> fileSubmissions;

	public String getInstitutionShortName() {
		return institutionShortName;
	}

	public void setInstitutionShortName(String institutionShortName) {
		this.institutionShortName = institutionShortName;
	}

	public String getInstitutionName() {
		return institutionName;
	}

	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<FileSubmissions> getFileSubmissions() {
		return fileSubmissions;
	}

	public void setFileSubmissions(List<FileSubmissions> fileSubmissions) {
		this.fileSubmissions = fileSubmissions;
	}

}
