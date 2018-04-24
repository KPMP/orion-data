package org.kpmp.upload;

import org.kpmp.dao.FileSubmission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileSubmissionsRepository extends CrudRepository<FileSubmission, Integer> {

	@SuppressWarnings("unchecked")
	public FileSubmission save(FileSubmission fileSubmission);
}
