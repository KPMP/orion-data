package org.kpmp.upload.deprecated;

import org.kpmp.dao.deprecated.InstitutionDemographics;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionRepository extends CrudRepository<InstitutionDemographics, Integer> {

	public InstitutionDemographics findByInstitutionName(String institutionName);

	public InstitutionDemographics findById(int id);

}
