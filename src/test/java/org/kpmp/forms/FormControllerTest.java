package org.kpmp.forms;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.JWTHandler;
import org.kpmp.logging.LoggingService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class FormControllerTest {

	@Mock
	private FormRepository repository;
	@Mock
	private JWTHandler jwtHandler;
	private FormController controller;
	@Mock
	private LoggingService logger;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new FormController(repository, jwtHandler, logger);
	}

	@After
	public void tearDown() throws Exception {
		controller = null;
	}

	@Test
	public void testGetFormDTDNoVersion() {
		Form expectedForm = mock(Form.class);
		when(repository.findTopByOrderByVersionDesc()).thenReturn(expectedForm);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(jwtHandler.getUserIdFromHeader(request)).thenReturn("userID");
		when(request.getRequestURI()).thenReturn("/v1/form");

		Form formDTD = controller.getFormDTD(request);

		assertEquals(expectedForm, formDTD);
		verify(logger).logInfoMessage(FormController.class, "userID", null, "/v1/form", "Request for all forms");
	}

	@Test
	public void testGetFormDTDWithVersion() {
		Form expectedForm = mock(Form.class);
		when(repository.findByVersion(1.0)).thenReturn(Arrays.asList(expectedForm));
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(jwtHandler.getUserIdFromHeader(request)).thenReturn("userID");
		when(request.getRequestURI()).thenReturn("/v1/form/version/1.0");

		Form formDTD = controller.getFormDTD(1.0, request);

		assertEquals(expectedForm, formDTD);
		verify(logger).logInfoMessage(FormController.class, "userID", null, "/v1/form/version/1.0",
				"Request for form with version: 1.0");
	}

}
