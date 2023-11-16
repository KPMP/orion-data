package org.kpmp.shibboleth;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
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

	public User getUser(HttpServletRequest request, JSONObject packageInfo) {

		String value = handleNull(request.getHeader("mail"));
        if (value == null || value == ""){
            value = (String) packageInfo.getJSONObject("submitter").get("email");
        }
		String email = encoder.convertFromLatin1(value);
		value = handleNull(request.getHeader("displayname"));
        if (value == null || value == ""){
            value = (String) packageInfo.getJSONObject("submitter").get("givenname") 
            + " " +  packageInfo.getJSONObject("submitter").get("sn");
        }
		String displayName = encoder.convertFromLatin1(value);
		value = handleNull(request.getHeader("givenname"));
        if (value == null || value == ""){
            value = (String) packageInfo.getJSONObject("submitter").get("firstName");
        }
		String firstName = encoder.convertFromLatin1(value);
		value = handleNull(request.getHeader("sn"));
        if (value == null || value == ""){
            value = (String) packageInfo.getJSONObject("submitter").get("lastName");
        }
		String lastName = encoder.convertFromLatin1(value);
		value = handleNull(request.getHeader("eppn"));
        if (value == null || value == ""){
            value = (String) packageInfo.getJSONObject("submitter").get("email");
        }
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