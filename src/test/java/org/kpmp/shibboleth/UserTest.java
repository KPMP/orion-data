package org.kpmp.shibboleth;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserTest {

	private User user;
	@Mock
	private HttpServletRequest request;
	@Mock
	private UTF8Encoder encoder;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		user = new User(request, encoder);
	}

	@After
	public void tearDown() throws Exception {
		user = null;
	}

	@Test
	public void testConstructor() throws Exception {
		when(request.getHeader("displayname")).thenReturn("displayName");
		when(encoder.convertFromLatin1("displayName")).thenReturn("display Name");
		when(request.getHeader("sn")).thenReturn("Robert");
		when(encoder.convertFromLatin1("Robert")).thenReturn("Roberto");
		when(request.getHeader("givenname")).thenReturn("Robbie");
		when(encoder.convertFromLatin1("Robbie")).thenReturn("Robby");
		when(request.getHeader("mail")).thenReturn("robbie@gmail.com");
		when(encoder.convertFromLatin1("robbie@gmail.com")).thenReturn("robby@gmail.com");

		user = new User(request, encoder);

		assertEquals("display Name", user.getDisplayName());
		assertEquals("Roberto", user.getLastName());
		assertEquals("Robby", user.getFirstName());
		assertEquals("robby@gmail.com", user.getEmail());
	}

	@Test
	public void testSetFirstName() {
		user.setFirstName("first name");
		assertEquals("first name", user.getFirstName());
	}

	@Test
	public void testSetLastName() {
		user.setLastName("last name");
		assertEquals("last name", user.getLastName());
	}

	@Test
	public void testSetDisplayName() {
		user.setDisplayName("display name");
		assertEquals("display name", user.getDisplayName());
	}

	@Test
	public void testSetEmail() {
		user.setEmail("email@stuff.org");
		assertEquals("email@stuff.org", user.getEmail());
	}

}
