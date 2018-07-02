package org.kpmp.upload;

import org.kpmp.dao.PackageType;
import org.springframework.data.repository.CrudRepository;

public interface PackageTypeRepository extends CrudRepository<PackageType, Integer> {

	public PackageType findByPackageType(String packageType);

}
