package org.miktmc.shibboleth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.json.JSONArray;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miktmc.users.User;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ShibbolethUserServiceTest {

	private ShibbolethUserService shibbolethUserService;
	@Mock
	private UTF8Encoder utf8Encoder;
	private AutoCloseable mocks;

	@BeforeEach
	public void setUp() throws Exception {
		mocks = MockitoAnnotations.openMocks(this);
		shibbolethUserService = new ShibbolethUserService(utf8Encoder);
	}

	@AfterEach
	public void tearDown() throws Exception {
		mocks.close();
		shibbolethUserService = null;
	}

	@Test
	public void testGetUser() throws UnsupportedEncodingException {
		HttpServletRequest request = mock(HttpServletRequest.class);

		when(request.getHeader("mail")).thenReturn("maninblack@jcash.com");
		when(request.getHeader("givenname")).thenReturn("Johnny");
		when(request.getHeader("sn")).thenReturn("Cash");
		when(request.getHeader("displayname")).thenReturn("Johnny Cash");
		when(request.getHeader("eppn")).thenReturn("shibId");
		HttpSession session = mock(HttpSession.class);
		when(session.getAttribute("roles")).thenReturn(new JSONArray());
		when(request.getSession(false)).thenReturn(session);
		when(utf8Encoder.convertFromLatin1("Johnny")).thenReturn("Johnny");
		when(utf8Encoder.convertFromLatin1("Cash")).thenReturn("Cash");
		when(utf8Encoder.convertFromLatin1("Johnny Cash")).thenReturn("Johnny Cash");
		when(utf8Encoder.convertFromLatin1("maninblack@jcash.com")).thenReturn("maninblack@jcash.com");
		when(utf8Encoder.convertFromLatin1("shibId")).thenReturn("shibId");

		User user2 = shibbolethUserService.getUser(request);

		assertEquals("maninblack@jcash.com", user2.getEmail());
		assertEquals("Johnny Cash", user2.getDisplayName());
		assertEquals("Cash", user2.getLastName());
		assertEquals("Johnny", user2.getFirstName());
		assertEquals("shibId", user2.getShibId());

        
	}

}