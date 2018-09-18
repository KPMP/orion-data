package org.kpmp.packages;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface PackageRepository extends MongoRepository<Package, String> {

	public List<Package> findAll();
	public Optional<Package> findById(String id);
}
