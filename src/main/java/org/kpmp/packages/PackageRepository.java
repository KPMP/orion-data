package org.kpmp.packages;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface PackageRepository extends MongoRepository<Package, String> {

	public List<Package> findAll();

	@SuppressWarnings("unchecked")
	public Package save(Package packageInfo);

	public Package findByPackageId(String packageId);
}
