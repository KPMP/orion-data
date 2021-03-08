package org.kpmp.apiTokens;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface TokenRepository extends MongoRepository<Token, String> {

	@SuppressWarnings("unchecked")
	public Token save(Token token);

	public Token findByShibId(String shibId);

	public Token findByTokenString(String tokenString);

}
