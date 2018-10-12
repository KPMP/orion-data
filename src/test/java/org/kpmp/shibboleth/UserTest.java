package org.kpmp.shibboleth;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserTest {

	private ShibbolethUser user;
	@Mock
	private HttpServletRequest request;
	@Mock
	private UTF8Encoder encoder;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		user = new ShibbolethUser(request, encoder);
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

		user = new ShibbolethUser(request, encoder);

		assertEquals("display Name", user.getDisplayName());
		assertEquals("Roberto", user.getLastName());
		assertEquals("Robby", user.getFirstName());
		assertEquals("robby@gmail.com", user.getEmail());
	}

	@Test
	public void testConstructor_handlesNullValues() throws Exception {
		when(request.getHeader("displayname")).thenReturn(null);
		when(request.getHeader("sn")).thenReturn(null);
		when(request.getHeader("givenname")).thenReturn(null);
		when(request.getHeader("mail")).thenReturn(null);
		UTF8Encoder testEncoder = mock(UTF8Encoder.class);

		new ShibbolethUser(request, testEncoder);

		verify(encoder, times(4)).convertFromLatin1("");
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
