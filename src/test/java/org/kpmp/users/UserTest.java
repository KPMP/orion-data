package org.kpmp.users;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

public class UserTest {

	private User testUser;

	@BeforeEach
	public void setUp() throws Exception {
		testUser = new User();
	}

	@AfterEach
	public void tearDown() throws Exception {
		testUser = null;
	}

	@Test
	public void testSetUserId() {
		testUser.setId("12345");
		assertEquals("12345", testUser.getId());
	}

	@Test
	public void testSetFirstName() {
		testUser.setFirstName("Ziggy");
		assertEquals("Ziggy", testUser.getFirstName());
	}

	@Test
	public void testSetLastName() {
		testUser.setLastName("Stardust");
		assertEquals("Stardust", testUser.getLastName());
	}

	@Test
	public void testSetEmail() {
		testUser.setEmail("ziggy@mars.com");
		assertEquals("ziggy@mars.com", testUser.getEmail());
	}

	@Test
	public void testSetDisplayName() {
        testUser.setDisplayName("Space Oddity");
		assertEquals("Space Oddity", testUser.getDisplayName());
	}

	@Test
	public void testToString() {
		testUser.setId("12345");
		testUser.setDisplayName("Space Oddity");
		testUser.setFirstName("Ziggy");
		testUser.setLastName("Stardust");
		testUser.setEmail("ziggy@mars.com");
		testUser.setShibId("ziggy@mars.com");
		assertEquals("userId: 12345" + ", firstName: Ziggy" + ", lastName: Stardust" + ", displayName: Space Oddity"
				+ ", email: ziggy@mars.com, shibId: ziggy@mars.com", testUser.toString());
	}

	@Test
	public void testGenerateJSON() throws Exception {
        List<String> userRoles = new ArrayList<>();
        userRoles.add("roles");
		testUser.setDisplayName("displayName");
		testUser.setEmail("emailAddress");
		testUser.setFirstName("firstName");
		testUser.setId("id");
		testUser.setLastName("lastName");
		testUser.setShibId("shibId");
        testUser.setRoles(userRoles);

		assertEquals("{\"firstName\":\"firstName\",\"lastName\":\"lastName\","
				+ "\"displayName\":\"displayName\",\"email\":\"emailAddress\",\"roles\":[\"roles\"]}", testUser.generateJSON());
	}

	@Test
	public void testGenerateJSONForApp() throws Exception {
        List<String> userRoles = new ArrayList<>();
        userRoles.add("roles");
		testUser.setDisplayName("displayName");
		testUser.setEmail("emailAddress");
		testUser.setFirstName("firstName");
		testUser.setId("id");
		testUser.setLastName("lastName");
		testUser.setShibId("shibId");
        testUser.setRoles(userRoles);

		assertEquals(
				"{\"id\":\"id\",\"firstName\":\"firstName\",\"lastName\":\"lastName\","
						+ "\"displayName\":\"displayName\",\"email\":\"emailAddress\",\"roles\":[\"roles\"],\"shibId\":\"shibId\"}",
				testUser.generateJSONForApp());
	}
}
