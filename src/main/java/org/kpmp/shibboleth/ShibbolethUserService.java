package org.kpmp.shibboleth;

import javax.servlet.http.HttpServletRequest;

import org.kpmp.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShibbolethUserService {

	private UTF8Encoder encoder;

	@Autowired
	public ShibbolethUserService(UTF8Encoder encoder) {
		this.encoder = encoder;
	}

	public User getUser(HttpServletRequest request) {

		String value = handleNull(request.getHeader("mail"));
		String email = encoder.convertFromLatin1(value);
		value = handleNull(request.getHeader("displayname"));
		String displayName = encoder.convertFromLatin1(value);
		value = handleNull(request.getHeader("givenname"));
		String firstName = encoder.convertFromLatin1(value);
		value = handleNull(request.getHeader("sn"));
		String lastName = encoder.convertFromLatin1(value);
		value = handleNull(request.getHeader("eppn"));
		String shibId = encoder.convertFromLatin1(value);

		User user = new User();
		user.setDisplayName(displayName);
		user.setLastName(lastName);
		user.setFirstName(firstName);
		user.setEmail(email);
		user.setShibId(shibId);

		return user;

	}

	private String handleNull(String value) {
		if (value == null) {
			return "";
		}
		return value;
	}
}