package org.kpmp.packages;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageRepository extends MongoRepository<Package, String> {

	// This method will not save any fields that aren't in the Package class and should not be used.
	@SuppressWarnings("unchecked")
	@Deprecated
	public Package save(Package packageInfo);

	public Package findByPackageId(String packageId);

}
