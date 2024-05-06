package org.kpmp.packages;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public interface StudyFileInfoRepository extends MongoRepository<StudyFileInfo, String> {

    public StudyFileInfo findByStudy(String study);
}
