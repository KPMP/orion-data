package org.kpmp.shibboleth;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.kpmp.users.User;
import org.springframework.stereotype.Service;

@Service
public class ShibbolethUserService {

	public User getUser(HttpServletRequest request, UTF8Encoder encoder) throws UnsupportedEncodingException {

		String value = handleNull(request.getHeader("mail"));
		String email = encoder.convertFromLatin1(value);
		value = handleNull(request.getHeader("displayname"));
		String displayName = encoder.convertFromLatin1(value);
		value = handleNull(request.getHeader("givenname"));
		String firstName = encoder.convertFromLatin1(value);
		value = handleNull(request.getHeader("sn"));
		String lastName = encoder.convertFromLatin1(value);

		User user = new User();
		user.setDisplayName(displayName);
		user.setLastName(lastName);
		user.setFirstName(firstName);
		user.setEmail(email);

		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String attribute = (String) headerNames.nextElement();
			System.err.println(attribute + ": " + request.getHeader(attribute));
		}

		return user;

	}

	private String handleNull(String value) {
		if (value == null) {
			return "";
		}
		return value;
	}
}
