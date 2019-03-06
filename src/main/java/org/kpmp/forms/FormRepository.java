package org.kpmp.forms;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface FormRepository extends MongoRepository<Form, String> {

	public List<Form> findAll();

}
