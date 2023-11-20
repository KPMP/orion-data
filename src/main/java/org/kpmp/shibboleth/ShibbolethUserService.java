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

    public User getUserNoHeaders(HttpServletRequest request, JSONObject packageInfo){
        String email = handleNull(request.getHeader("mail"));
        if (email == ""){
            email = packageInfo.getString("submitterEmail");
        }
		email = encoder.convertFromLatin1(email);

		String displayName = handleNull(request.getHeader("displayname"));
        if (displayName == ""){
            displayName = packageInfo.getString("submitterFirstName") 
            + " " +  packageInfo.getString("submitterLastName");
        }
		displayName = encoder.convertFromLatin1(displayName);

		String firstName = handleNull(request.getHeader("givenname"));
        if (firstName == ""){
            firstName = packageInfo.getString("submitterFirstName");
        }
		firstName = encoder.convertFromLatin1(firstName);

		String lastName = handleNull(request.getHeader("sn"));
        if (lastName == ""){
            lastName = packageInfo.getString("submitterLastName");
        }
		lastName = encoder.convertFromLatin1(lastName);
        
		String value = handleNull(request.getHeader("eppn"));
		String shibId = encoder.convertFromLatin1(value);

		User user = new User();
		user.setDisplayName(displayName);
		user.setLastName(lastName);
		user.setFirstName(firstName);
		user.setEmail(email);
		user.setShibId(shibId);

		return user;

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