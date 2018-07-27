package org.kpmp.upload;

import javax.transaction.Transactional;

import org.kpmp.dao.deprecated.PackageTypeOther;
import org.springframework.data.repository.CrudRepository;

public interface PackageTypeOtherRepository extends CrudRepository<PackageTypeOther, Integer> {

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public PackageTypeOther save(PackageTypeOther packageTypeOther);

}
