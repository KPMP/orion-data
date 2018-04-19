package org.kpmp.upload;

import org.kpmp.dao.FileSubmissions;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileSubmissionsRepository extends CrudRepository<FileSubmissions, Integer> {

	@SuppressWarnings("unchecked")
	public FileSubmissions save(FileSubmissions fileSubmission);
}
