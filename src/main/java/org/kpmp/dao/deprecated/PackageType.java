package org.kpmp.dao.deprecated;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "package_type")
public class PackageType {

	@Id
	@Column(name = "id")
	private int id;
	@Column(name = "package_type")
	private String packageType;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "packageType")
	private List<UploadPackage> uploadPackages;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	public List<UploadPackage> getUploadPackages() {
		return uploadPackages;
	}

	public void setUploadPackages(List<UploadPackage> uploadPackages) {
		this.uploadPackages = uploadPackages;
	}

}
