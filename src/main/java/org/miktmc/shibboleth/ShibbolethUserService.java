package org.miktmc.shibboleth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.miktmc.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

		List<String> roleList = new ArrayList<>();
		HttpSession session = request.getSession(false);
        String firstName = "";
        String lastName = "";
		if (session != null) {
			JSONArray roles = (JSONArray)session.getAttribute("roles");
			roleList = JSONArrayToList(roles);
            firstName = (String)session.getAttribute("firstName");
            lastName = (String)session.getAttribute("lastName");
		}


		String value = handleNull(request.getHeader("mail"));
		String email = encoder.convertFromLatin1(value);
		value = handleNull(request.getHeader("displayname"));
		String displayName = firstName + " " + lastName;
		value = handleNull(request.getHeader("eppn"));
		String shibId = encoder.convertFromLatin1(value);

		User user = new User();
		user.setDisplayName(displayName);
		user.setLastName(lastName);
		user.setFirstName(firstName);
		user.setEmail(email);
		user.setShibId(shibId);
		user.setRoles(roleList);

		return user;

	}

	private List<String> JSONArrayToList(JSONArray array) {
		List<String> roles = new ArrayList<>();
		if (array == null) {
			return roles;
		}
		for (int i = 0; i < array.length(); i++) {  
			String role = array.optString(i);
			roles.add(role);
			System.err.println(role);
		}
		return roles;
	}

	private String handleNull(String value) {
		if (value == null) {
			return "";
		}
		return value;
	}
}