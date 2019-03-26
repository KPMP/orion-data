package org.kpmp.packages;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageTypeIconRepository extends MongoRepository<PackageTypeIcon, String> {
    public List<PackageTypeIcon> findAll();
}
