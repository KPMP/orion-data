package org.kpmp.upload;

import org.kpmp.dao.UploadPackage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadPackageRepository extends CrudRepository<UploadPackage, Integer> {

	@SuppressWarnings("unchecked")
	@Override
	public UploadPackage save(UploadPackage uploadPackage);

	@Override
	public void delete(UploadPackage uploadPackage);

}
