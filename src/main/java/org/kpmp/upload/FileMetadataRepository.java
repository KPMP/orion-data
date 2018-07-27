package org.kpmp.upload;

import javax.transaction.Transactional;

import org.kpmp.dao.deprecated.FileMetadataEntries;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMetadataRepository extends CrudRepository<FileMetadataEntries, Integer> {

	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public FileMetadataEntries save(FileMetadataEntries fileMetadata);

}
