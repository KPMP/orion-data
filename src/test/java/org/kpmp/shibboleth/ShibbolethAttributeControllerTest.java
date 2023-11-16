package org.kpmp.shibboleth;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.logging.LoggingService;
import org.kpmp.users.User;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ShibbolethAttributeControllerTest {

	private ShibbolethAttributeController controller;
	@Mock
	private ShibbolethUserService shibbolethUserService;
	@Mock
	private LoggingService logger;
	private AutoCloseable mocks;

	@Before
	public void setUp() throws Exception {
		mocks = MockitoAnnotations.openMocks(this);
		controller = new ShibbolethAttributeController(shibbolethUserService, logger);
	}

	@After
	public void tearDown() throws Exception {
		mocks.close();
		controller = null;
	}

	@Test
	public void testGetAttributes() throws UnsupportedEncodingException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		User testUser = new User();
		when(shibbolethUserService.getUser(request,null)).thenReturn(testUser);

		assertEquals(testUser, controller.getAttributes(request));
		verify(logger).logInfoMessage(ShibbolethAttributeController.class, null,
				"Retrieving user information from shibboleth", request);
	}

}