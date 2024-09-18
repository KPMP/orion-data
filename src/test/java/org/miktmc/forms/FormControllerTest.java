package org.miktmc.forms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miktmc.logging.LoggingService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class FormControllerTest {

	@Mock
	private FormRepository repository;
	private FormController controller;
	@Mock
	private LoggingService logger;
	private AutoCloseable mocks;

	@BeforeEach
	public void setUp() throws Exception {
		mocks = MockitoAnnotations.openMocks(this);
		controller = new FormController(repository, logger);
	}

	@AfterEach
	public void tearDown() throws Exception {
		mocks.close();
		controller = null;
	}

	@Test
	public void testGetFormDTDNoVersion() {
		Form expectedForm = mock(Form.class);
		when(repository.findTopByOrderByVersionDesc()).thenReturn(expectedForm);
		HttpServletRequest request = mock(HttpServletRequest.class);

		Form formDTD = controller.getFormDTD(request);

		assertEquals(expectedForm, formDTD);
		verify(logger).logInfoMessage(FormController.class, null, "Request for all forms", request);
	}

	@Test
	public void testGetFormDTDWithVersion() {
		Form expectedForm = mock(Form.class);
		when(repository.findByVersion(1.0)).thenReturn(Arrays.asList(expectedForm));
		HttpServletRequest request = mock(HttpServletRequest.class);

		Form formDTD = controller.getFormDTD(1.0, request);

		assertEquals(expectedForm, formDTD);
		verify(logger).logInfoMessage(FormController.class, null, "Request for form with version: 1.0", request);
	}

}
