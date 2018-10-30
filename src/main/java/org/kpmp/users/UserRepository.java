package org.kpmp.users;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface UserRepository extends MongoRepository<User, String> {

	public <T extends User> T save(User user);

	public User findByEmail(String email);

}
