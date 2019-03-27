package org.kpmp.releases;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface ReleaseRepository extends MongoRepository<Release, String> {
    List<Release> findAll();
    Release findByVersion(String version);
}
