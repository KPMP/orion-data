package org.kpmp.forms;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface FormRepository extends MongoRepository<Form, String> {

	public List<Form> findByVersion(Double version);
	public Form findTopByOrderByVersionDesc();

}
