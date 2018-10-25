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

public class ShibbolethAtttributeControllerTest {

	private ShibbolethAttributeController controller;
	@Mock
	private UTF8Encoder encoder;
	@Mock
	private ShibbolethUserService shibbolethUserService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new ShibbolethAttributeController(encoder, shibbolethUserService);
	}

	@After
	public void tearDown() throws Exception {
		controller = null;
	}

	@Test
	public void testGetAttributes() throws UnsupportedEncodingException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		User testUser = new User();
		when(shibbolethUserService.getUser(request, encoder)).thenReturn(testUser);
		assertEquals(testUser, controller.getAttributes(request));
	}

}
