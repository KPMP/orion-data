package org.kpmp.apiTokens;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

	private TokenRepository tokenRepository;

	public TokenService(TokenRepository tokenRepository) {
		this.tokenRepository = tokenRepository;
	}

	public Token getOrSetToken(String shibId) {
		Token resultToken = tokenRepository.findByShibId(shibId);
		if (resultToken != null) {
			return resultToken;
		} else {
			Token token = generateToken(shibId);
			tokenRepository.save(token);
			return token;
		}
	}

	public Token generateToken(String shibId) {
		Token token = new Token();
		token.setShibId(shibId);
		token.setActive(true);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 1);
		Date nextYear = cal.getTime();
		token.setExpiration(nextYear);
		int length = 44;
		boolean useLetters = true;
		boolean useNumbers = true;
		String tokenString = RandomStringUtils.random(length, useLetters, useNumbers);
		token.setTokenString(tokenString);
		return token;
	}

	public Boolean checkExpired(Token token) {
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		return today.compareTo(token.getExpiration()) > 0;
	}

	public Boolean checkToken(Token token) {
		return !checkExpired(token) && token.getActive();
	}

	public Token getTokenByTokenString(String tokenString) {
		return tokenRepository.findByTokenString(tokenString);
	}

	public Boolean checkAndValidate(String tokenString) {
		Token token = tokenRepository.findByTokenString(tokenString);
		if (token != null) {
			return checkToken(token);
		} else {
			return false;
		}
	}
}
