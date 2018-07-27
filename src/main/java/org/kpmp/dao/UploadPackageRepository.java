package org.kpmp.dao;

import java.util.List;

import org.kpmp.view.UploadPackage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface UploadPackageRepository extends MongoRepository<UploadPackage, String> {

	public List<UploadPackage> findAll();
}
