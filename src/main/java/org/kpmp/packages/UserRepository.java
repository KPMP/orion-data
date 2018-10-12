package org.kpmp.packages;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface UserRepository extends MongoRepository<User, String> {

    public User save(User user);

    public User findByEmailAddress(String emailAddress);

}
