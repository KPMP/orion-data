package org.kpmp.upload;

import javax.transaction.Transactional;

import org.kpmp.dao.deprecated.SubmitterDemographics;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmitterRepository extends CrudRepository<SubmitterDemographics, Integer> {

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public SubmitterDemographics save(SubmitterDemographics submitter);

	public SubmitterDemographics findById(int id);

}
