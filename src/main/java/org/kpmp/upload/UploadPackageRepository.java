package org.kpmp.upload;

import java.util.List;

import javax.transaction.Transactional;

import org.kpmp.dao.UploadPackage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadPackageRepository extends CrudRepository<UploadPackage, Integer> {

	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public UploadPackage save(UploadPackage uploadPackage);

	public UploadPackage findById(int id);
	public List<UploadPackage> findAll();

}
