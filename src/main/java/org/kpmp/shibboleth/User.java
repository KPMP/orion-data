package org.kpmp.shibboleth;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

@Component
class User {

	private String displayName;
	private String firstName;
	private String lastName;
	private String email;

	// This can be construed as an anti-pattern, but since we are simply using
	// the request to construct this object so that we can pass the information
	// back to the application, and we made this object protected, we should be
	// safe
	public User(HttpServletRequest request, UTF8Encoder encoder) throws UnsupportedEncodingException {
		this.displayName = encoder.convertFromLatin1(request.getHeader("displayname"));
		this.firstName = encoder.convertFromLatin1(request.getHeader("givenname"));
		this.lastName = encoder.convertFromLatin1(request.getHeader("sn"));
		this.email = encoder.convertFromLatin1(request.getHeader("mail"));
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
