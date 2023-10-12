package org.kpmp.users;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonPropertyOrder({ "id", "firstName", "lastName", "displayName", "email" })
@Document(collection = "users")
public class User {

	@Id
	private String id;
	private String firstName;
	private String lastName;
	private String displayName;
	private String email;
	private String shibId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getDisplayName() {
		return this.getFirstName() + " " + this.getLastName();
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String emailAddress) {
		this.email = emailAddress;
	}

	@Override
	public String toString() {
		return "userId: " + id + ", firstName: " + firstName + ", lastName: " + lastName + ", displayName: "
				+ displayName + ", email: " + email + ", shibId: " + shibId;
	}

	public String generateJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.addMixIn(User.class, UserJsonMixin.class);
		return mapper.writeValueAsString(this);
	}

	// We want the user id in the json for the front-end, but not for the
	// metadata.json...hence the separate method
	public String generateJSONForApp() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

	public String getShibId() {
		return shibId;
	}

	public void setShibId(String shibId) {
		this.shibId = shibId;
	}
}
