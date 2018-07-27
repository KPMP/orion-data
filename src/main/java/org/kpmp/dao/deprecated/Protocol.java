package org.kpmp.dao.deprecated;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "protocol")
public class Protocol {

	@Id
	@Column(name = "id")
	private int id;
	@Column(name = "protocol")
	private String protocol;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "protocol")
	private List<UploadPackage> uploadPackages;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public List<UploadPackage> getUploadPackages() {
		return uploadPackages;
	}

	public void setUploadPackages(List<UploadPackage> uploadPackages) {
		this.uploadPackages = uploadPackages;
	}

}
