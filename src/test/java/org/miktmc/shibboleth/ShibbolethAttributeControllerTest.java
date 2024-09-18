package org.miktmc.shibboleth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miktmc.logging.LoggingService;
import org.miktmc.users.User;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ShibbolethAttributeControllerTest {

	private ShibbolethAttributeController controller;
	@Mock
	private ShibbolethUserService shibbolethUserService;
	@Mock
	private LoggingService logger;
	private AutoCloseable mocks;

	@BeforeEach
	public void setUp() throws Exception {
		mocks = MockitoAnnotations.openMocks(this);
		controller = new ShibbolethAttributeController(shibbolethUserService, logger);
	}

	@AfterEach
	public void tearDown() throws Exception {
		mocks.close();
		controller = null;
	}

	@Test
	public void testGetAttributes() throws UnsupportedEncodingException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		User testUser = new User();
		when(shibbolethUserService.getUser(request)).thenReturn(testUser);

		assertEquals(testUser, controller.getAttributes(request));
		verify(logger).logInfoMessage(ShibbolethAttributeController.class, null,
				"Retrieving user information from shibboleth", request);
	}

}