package org.kpmp.shibboleth;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.users.User;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ShibbolethUserServiceTest {

	private ShibbolethUserService shibbolethUserService;
	@Mock
	private UTF8Encoder utf8Encoder;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		shibbolethUserService = new ShibbolethUserService(utf8Encoder);
	}

	@After
	public void tearDown() throws Exception {
		shibbolethUserService = null;
	}

	@Test
	public void testGetUser() throws UnsupportedEncodingException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader("mail")).thenReturn("maninblack@jcash.com");
		when(request.getHeader("givenname")).thenReturn("Johnny");
		when(request.getHeader("sn")).thenReturn("Cash");
		when(request.getHeader("displayname")).thenReturn("Johnny Cash");
		when(utf8Encoder.convertFromLatin1("Johnny")).thenReturn("Johnny");
		when(utf8Encoder.convertFromLatin1("Cash")).thenReturn("Cash");
		when(utf8Encoder.convertFromLatin1("Johnny Cash")).thenReturn("Johnny Cash");
		when(utf8Encoder.convertFromLatin1("maninblack@jcash.com")).thenReturn("maninblack@jcash.com");

		User user = shibbolethUserService.getUser(request);

		assertEquals("maninblack@jcash.com", user.getEmail());
		assertEquals("Johnny Cash", user.getDisplayName());
		assertEquals("Cash", user.getLastName());
		assertEquals("Johnny", user.getFirstName());

	}

}