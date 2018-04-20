package org.kpmp.upload;

import org.kpmp.dao.SubmitterDemographics;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmitterRepository extends CrudRepository<SubmitterDemographics, Integer> {

	@SuppressWarnings("unchecked")
	@Override
	public SubmitterDemographics save(SubmitterDemographics submitter);

}
