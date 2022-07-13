package org.kpmp.packages;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public interface PackageRepository extends MongoRepository<Package, String> {

	@Deprecated
	@SuppressWarnings("unchecked")
	public Package save(Package packageInfo);

	public Package findByPackageId(String packageId);

}
