package org.miktmc.users;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface UserRepository extends MongoRepository<User, String> {

	@SuppressWarnings("unchecked")
	public User save(User user);

	public User findByEmail(String email);

	public List<User> findAll();

	public Optional<User> findById(String id);

}
