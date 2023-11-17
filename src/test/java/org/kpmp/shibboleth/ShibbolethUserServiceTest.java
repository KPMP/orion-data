package org.kpmp.shibboleth;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
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
	private AutoCloseable mocks;

	@Before
	public void setUp() throws Exception {
		mocks = MockitoAnnotations.openMocks(this);
		shibbolethUserService = new ShibbolethUserService(utf8Encoder);
	}

	@After
	public void tearDown() throws Exception {
		mocks.close();
		shibbolethUserService = null;
	}

	@Test
	public void testGetUser() throws UnsupportedEncodingException {
		HttpServletRequest request = mock(HttpServletRequest.class);
        String userInfoString = "{\"submitterFirstName\":\"Space\",\"submitterLastName\":\"Oddity\",\"submitterEmail\":\"spaceOddity@email.com\"}";
        JSONObject userInfo = new JSONObject(userInfoString);

        
        when(request.getHeader("givenname")).thenReturn(null);
		when(request.getHeader("sn")).thenReturn(null);
        when(request.getHeader("displayname")).thenReturn(null);
        when(utf8Encoder.convertFromLatin1("Space")).thenReturn("Space");
		when(utf8Encoder.convertFromLatin1("Oddity")).thenReturn("Oddity");
		when(utf8Encoder.convertFromLatin1("Space Oddity")).thenReturn("Space Oddity");
		when(utf8Encoder.convertFromLatin1("spaceOddity@email.com")).thenReturn("spaceOddity@email.com");

        User user1 = shibbolethUserService.getUser(request, userInfo);

        assertEquals("Space", user1.getFirstName());
        assertEquals("Oddity", user1.getLastName());
        assertEquals("spaceOddity@email.com", user1.getEmail());
        assertEquals("Space Oddity", user1.getDisplayName());
        
		when(request.getHeader("mail")).thenReturn("maninblack@jcash.com");
		when(request.getHeader("givenname")).thenReturn("Johnny");
		when(request.getHeader("sn")).thenReturn("Cash");
		when(request.getHeader("displayname")).thenReturn("Johnny Cash");
		when(request.getHeader("eppn")).thenReturn("shibId");
		when(utf8Encoder.convertFromLatin1("Johnny")).thenReturn("Johnny");
		when(utf8Encoder.convertFromLatin1("Cash")).thenReturn("Cash");
		when(utf8Encoder.convertFromLatin1("Johnny Cash")).thenReturn("Johnny Cash");
		when(utf8Encoder.convertFromLatin1("maninblack@jcash.com")).thenReturn("maninblack@jcash.com");
		when(utf8Encoder.convertFromLatin1("shibId")).thenReturn("shibId");

		User user2 = shibbolethUserService.getUser(request,null);

		assertEquals("maninblack@jcash.com", user2.getEmail());
		assertEquals("Johnny Cash", user2.getDisplayName());
		assertEquals("Cash", user2.getLastName());
		assertEquals("Johnny", user2.getFirstName());
		assertEquals("shibId", user2.getShibId());

        
	}

}