package org.kpmp.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileSubmissionsRepository extends CrudRepository<FileSubmission, Integer> {

	@SuppressWarnings("unchecked")
	public FileSubmission save(FileSubmission fileSubmission);

	public List<FileSubmission> findAll();
}
